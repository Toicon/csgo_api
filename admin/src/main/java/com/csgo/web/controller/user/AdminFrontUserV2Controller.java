package com.csgo.web.controller.user;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.user.SearchUserPlusCondition;
import com.csgo.config.ZBTProperties;
import com.csgo.constants.SystemConfigConstants;
import com.csgo.constants.UserConstants;
import com.csgo.domain.enums.YesOrNoEnum;
import com.csgo.domain.plus.config.SystemConfig;
import com.csgo.domain.plus.order.OrderRecord;
import com.csgo.domain.plus.user.*;
import com.csgo.domain.plus.withdraw.WithdrawPropPriceDTO;
import com.csgo.domain.user.User;
import com.csgo.mapper.plus.user.UserPlusMapper;
import com.csgo.modular.ig.service.IgService;
import com.csgo.service.AdminUserService;
import com.csgo.service.UserAnchorWhiteService;
import com.csgo.service.UserService;
import com.csgo.service.config.SystemConfigService;
import com.csgo.service.order.OrderRecordService;
import com.csgo.service.withdraw.WithdrawPropService;
import com.csgo.support.DataConverter;
import com.csgo.support.GlobalConstants;
import com.csgo.support.StandardExceptionCode;
import com.csgo.support.ZBT.CheckCreateDTO;
import com.csgo.support.ZBT.ZBTResult;
import com.csgo.util.BeanUtilsEx;
import com.csgo.util.HttpsUtil2;
import com.csgo.util.SecuritySHA1Utils;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.user.AddUserRequest;
import com.csgo.web.request.user.EditUserRequest;
import com.csgo.web.request.user.SearchUserPlusRequest;
import com.csgo.web.response.user.UserPlusResponse;
import com.csgo.web.response.user.WhiteListResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;
import com.echo.framework.support.jackson.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Api
@RequestMapping("/user")
@Slf4j
public class AdminFrontUserV2Controller extends BackOfficeController {

    @Autowired
    private UserPlusMapper userPlusMapper;
    @Autowired
    private UserService service;
    @Autowired
    private OrderRecordService orderRecordService;
    @Autowired
    private WithdrawPropService withdrawPropService;
    @Autowired
    private AdminUserService adminUserService;
    @Value("${baseExtensionUrl}")
    private String baseExtensionUrl;
    @Value("${faceUrl}")
    private String faceUrl;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private ZBTProperties properties;
    @Autowired
    private IgService igService;

    @Autowired
    private UserAnchorWhiteService userAnchorWhiteService;

    @GetMapping("/extension")
    public BaseResponse<String> getExtension(@RequestParam("type") Integer type) {
        String selfExtensionCode = RandomStringUtils.randomAlphanumeric(2) + RandomStringUtils.randomNumeric(4);
        if (type.equals(1)) {
            return BaseResponse.<String>builder().data(baseExtensionUrl + selfExtensionCode).get();
        }

        return BaseResponse.<String>builder().data(selfExtensionCode).get();
    }

    @GetMapping("/roll")
    public BaseResponse<List<UserPlusResponse>> findAllRollUser(@RequestParam("name") String name) {
        return BaseResponse.<List<UserPlusResponse>>builder().data(service.findAllInsideUser(name).stream().map(adminUserPlus -> {
            UserPlusResponse response = new UserPlusResponse();
            BeanUtils.copyProperties(adminUserPlus, response);
            return response;
        }).collect(Collectors.toList())).get();
    }

    @GetMapping("/{id}")
    public BaseResponse<UserPlus> getByUserId(@PathVariable("id") Integer id) {
        UserPlus user = userPlusMapper.selectById(id);
        return BaseResponse.<UserPlus>builder().data(user).get();
    }

    @PostMapping
    @Log(desc = "添加用户")
    public BaseResponse<Void> add(@Valid @RequestBody AddUserRequest req) {
        UserPlus user = new UserPlus();
        if (req.getFlag() != null && YesOrNoEnum.YES.getCode().equals(req.getFlag()) && req.getAdminUserId() == null) {
            throw new ApiException(StandardExceptionCode.ADD_USER_FAILURE, "关联账号不能为空");
        }
        if (StringUtils.isEmpty(req.getUserName())) {
            throw new ApiException(StandardExceptionCode.ADD_USER_FAILURE, "用户名不能为空");
        }
        if (StringUtils.isEmpty(req.getPassword())) {
            throw new ApiException(StandardExceptionCode.ADD_USER_FAILURE, "密码不能为空");
        }
        if (StringUtils.isEmpty(req.getName())) {
            throw new ApiException(StandardExceptionCode.ADD_USER_FAILURE, "昵称不能为空");
        }
        boolean isExits = service.checkNameExits(null, req.getName());
        if (isExits) {
            throw new ApiException(StandardExceptionCode.EDIT_USER_FAILURE, "昵称已被使用，请重新填写！");
        }
        boolean hasChild = service.checkDepartHasChild(req.getAdminUserId());
        if (hasChild) {
            throw new ApiException(StandardExceptionCode.EDIT_USER_FAILURE, "关联账号所属部门存在子部门！");
        }
        validCapRestrictions(req.getCapRestrictions());
        User userBean = service.getUserByUserName(req.getUserName().trim());
        if (userBean != null) {
            throw new ApiException(StandardExceptionCode.ADD_USER_FAILURE, "该手机号已经注册过");
        }

        BeanUtilsEx.copyProperties(req, user);
        if (!StringUtils.isEmpty(req.getExtensionUrl())) {
            User userUrl = service.getUserByExtensionUrl(req.getExtensionUrl().trim());
            if (userUrl != null) {
                throw new ApiException(StandardExceptionCode.ADD_USER_FAILURE, "推广链接已存在");
            }
            user.setExtensionUrl(req.getExtensionUrl());
        }
        if (!StringUtils.isEmpty(req.getExtensionCode())) {
            User userCode = service.getUserByExtensionCode(req.getExtensionCode().trim());
            if (userCode != null) {
                throw new ApiException(StandardExceptionCode.ADD_USER_FAILURE, "推广码已存在");
            }
            user.setExtensionCode(req.getExtensionCode());
        }
        if (!StringUtils.isEmpty(req.getTag())) {
            user.setTag(Tag.valueOf(req.getTag()));
        }

        try {
            user.setUserNumber(SecuritySHA1Utils.shaEncode(req.getUserName()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        user.setPhone(req.getUserName());
        user.setImg(faceUrl);
        user.setExtraNum(0);
        user.setIsroll(false);
        user.setIsDelete(1);
        user.setGear(1);
        user.setBattle(false);
        user.setCt(siteContext.getCurrentUser().getName());

        // 内部账号默认添加幸运值
        if (GlobalConstants.INTERNAL_USER_FLAG == req.getFlag()) {
            SystemConfig config = systemConfigService.get(SystemConfigConstants.DEFAULT_INNER_USER_LUCKY);
            if (config != null) {
                user.setLucky(new BigDecimal(config.getValue()));
            }
        } else {
            //散户账号设置未分类主播id
            user.setParentId(UserConstants.UNCLASSIFIED_USER_ID);
        }
        service.insert(user, req.getAdminUserId());
        return BaseResponse.<Void>builder().get();
    }

    @PutMapping("/{id}")
    @Log(desc = "更新用户")
    public BaseResponse<Void> update(@PathVariable("id") int id, @Valid @RequestBody EditUserRequest req) {
        boolean hasChild = service.checkDepartHasChild(req.getAdminUserId());
        if (hasChild) {
            throw new ApiException(StandardExceptionCode.EDIT_USER_FAILURE, "关联账号所属部门存在子部门！");
        }
        UserPlus user = service.getUserPlus(id);
        String before = null;
        if (StringUtils.hasText(req.getSteam()) && StringUtils.hasText(user.getSteam())
                && !user.getSteam().equals(req.getSteam())) {
            before = user.getSteam();

            //验证steam的合法性
            CheckCreateDTO checkCreateDTO = new CheckCreateDTO();
            checkCreateDTO.setAppId(properties.getAppId());
            checkCreateDTO.setTradeUrl(req.getSteam());
            checkCreateDTO.setType(1);
            String result = HttpsUtil2.getJsonData(JSON.toJSON(checkCreateDTO), properties.getSteamCheck());

            ZBTResult zbtResult = JSON.fromJSON(result, ZBTResult.class);
            log.info("[Steam链接校验] url:{} data:{}", req.getSteam(), JSON.toJSON(zbtResult));
            if (null == zbtResult || !zbtResult.getSuccess()) {
                log.error("[Steam链接校验] url:{} 不合法 data:{}", req.getSteam(), JSON.toJSON(zbtResult));
                throw new ApiException(StandardExceptionCode.STEAM_NOT_EXISTS, "STEAM链接不合法");
            }

            //igService.checkSteamTradeLinks(req.getSteam());
            // yyService.checkSteamTradeLinks(req.getSteam());
        }
        BeanUtilsEx.copyProperties(req, user);
        if (req.getIsroll() == null) {
            if (StringUtils.isEmpty(req.getUserName())) {
                throw new ApiException(StandardExceptionCode.EDIT_USER_FAILURE, "用户名不能为空");
            }
            if (StringUtils.isEmpty(req.getPassword())) {
                throw new ApiException(StandardExceptionCode.EDIT_USER_FAILURE, "密码不能为空");
            }
            if (StringUtils.isEmpty(req.getName())) {
                throw new ApiException(StandardExceptionCode.EDIT_USER_FAILURE, "昵称不能为空");
            }

            User userBean = service.getUserByUserName(req.getUserName().trim());
            if (userBean != null && !userBean.getId().equals(id)) {
                throw new ApiException(StandardExceptionCode.EDIT_USER_FAILURE, "该手机号已经注册过");
            }

        }
        if (StringUtils.hasText(req.getParentUserName())) {
            User parentUser = service.getUserByUserName(req.getParentUserName());
            if (null == parentUser) {
                throw new ApiException(StandardExceptionCode.EDIT_USER_FAILURE, "上级账号不存在");
            }
            user.setParentId(parentUser.getId());
        }
        if (!StringUtils.isEmpty(req.getExtensionUrl())) {
            User userUrl = service.getUserByExtensionUrl(req.getExtensionUrl().trim());
            if (userUrl != null && !userUrl.getId().equals(id)) {
                throw new ApiException(StandardExceptionCode.EDIT_USER_FAILURE, "推广链接已存在");
            }
            user.setExtensionUrl(req.getExtensionUrl());
        }
        if (!StringUtils.isEmpty(req.getExtensionCode())) {
            User userCode = service.getUserByExtensionCode(req.getExtensionCode().trim());
            if (userCode != null && !userCode.getId().equals(id)) {
                throw new ApiException(StandardExceptionCode.EDIT_USER_FAILURE, "推广码已存在");
            }
            user.setExtensionCode(req.getExtensionCode());
        }
        if (!StringUtils.isEmpty(req.getTag())) {
            user.setTag(Tag.valueOf(req.getTag()));
        }
        if (null != req.getGear()) {
            user.setGear(req.getGear());
        }
        validCapRestrictions(req.getCapRestrictions());
        user.setBattle(req.isBattle());
        user.setUpdatedAt(new Date());
        if (req.getCapRestrictions() == null) {
            user.setCapRestrictions(null);
        }
        service.update(user, before, siteContext.getCurrentUser().getName(), req.getAdminUserId());
        return BaseResponse.<Void>builder().get();
    }

    private void validCapRestrictions(BigDecimal capRestrictionsReq) {
        BigDecimal capRestrictions = Optional.ofNullable(capRestrictionsReq).orElse(BigDecimal.ZERO);
        AdminUserPlus adminUser = adminUserService.get(siteContext.getCurrentUser().getId());
        BigDecimal capRestrictionsLimit = Optional.ofNullable(adminUser.getCapRestrictions()).orElse(BigDecimal.ZERO);
        if (capRestrictionsLimit.compareTo(capRestrictions) < 0) {
            throw new ApiException(StandardExceptionCode.ADD_USER_FAILURE, "封顶限制超过你当前限制");
        }
    }

    /**
     * 根据用户ID删除到对应的用户
     *
     * @return
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Void> delete(@PathVariable("id") int id) {
        User user = service.getUserById(id);
        if (user == null) {
            throw new ApiException(StandardExceptionCode.DELETE_USER_FAILURE, "找不到要删除的用户");
        }
        if (user.getId().intValue() == UserConstants.UNCLASSIFIED_USER_ID) {
            throw new ApiException(StandardExceptionCode.DELETE_USER_FAILURE, "未归属主播账号不能删除");
        }
        user.setIsDelete(0);
        service.update(user, id);
        return BaseResponse.<Void>builder().get();
    }

    @PostMapping("/pagination")
    @Log(desc = "查询用户列表")
    public PageResponse<UserPlusResponse> pagination(@Validated @RequestBody SearchUserPlusRequest request) {
        SearchUserPlusCondition condition = DataConverter.to(SearchUserPlusCondition.class, request, request.getSort(), request.getSortBy());
        if (!StringUtils.isEmpty(request.getTag())) {
            condition.setTag(Tag.valueOf(request.getTag()));
        }
        if (!StringUtils.isEmpty(request.getEndTime())) {
            condition.setEndTime(request.getEndTime() + GlobalConstants.END_TIME);
        }
        Page<UserPlusDTO> pagination = service.pagination(condition);
        List<Integer> userIds = pagination.getRecords().stream().map(UserPlusDTO::getId).collect(Collectors.toList());
        Map adminUsers = service.getAdminUserIdByUserId(userIds);
        Map<Integer, BigDecimal> rechargeMap = orderRecordService.findByUserIds(userIds).stream().collect(Collectors.groupingBy(OrderRecord::getUserId, Collectors.reducing(BigDecimal.ZERO, OrderRecord::getOrderAmount, BigDecimal::add)));
        Map<Integer, BigDecimal> withdrawMap = withdrawPropService.findByUserIds(userIds).stream().collect(Collectors.groupingBy(WithdrawPropPriceDTO::getUserId, Collectors.reducing(BigDecimal.ZERO, WithdrawPropPriceDTO::getPrice, BigDecimal::add)));
        return DataConverter.to(user -> {
            UserPlusResponse response = new UserPlusResponse();
            BeanUtilsEx.copyProperties(user, response);
            if (null != user.getParentId()) {
                UserPlus parent = service.getUserPlus(user.getParentId());
                if (null != parent) {
                    response.setParentName(parent.getName());
                    response.setParentUserName(parent.getUserName());
                }
            }
            BigDecimal recharge = Optional.ofNullable(rechargeMap.get(user.getId())).orElse(BigDecimal.ZERO);
            response.setInnerRecharge(service.validateInnerRecharge(user.getId()));
            response.setWhiteInnerRecharge(service.validateWhiteInnerRecharge(user.getId()));
            response.setPayMoney(recharge);
            response.setProfit(Optional.ofNullable(withdrawMap.get(user.getId())).orElse(BigDecimal.ZERO).subtract(recharge).subtract(user.getBalance()));
            if (adminUsers != null && adminUsers.containsKey(user.getId())) {
                response.setAdminUserId((Integer) (adminUsers.get(user.getId())));
            }
            //判断是否是测试开箱白名单
            response.setWhiteAnchorRecharge(userAnchorWhiteService.validateWhiteAnchor(user.getId()));
            return response;
        }, pagination);
    }

    @PutMapping("/account/frozen/{userId}")
    @Log(desc = "冻结用户")
    public BaseResponse<Void> frozen(@PathVariable("userId") Integer userId, @RequestParam("frozen") Boolean frozen) {
        UserPlus userPlus = service.getUserPlus(userId);
        userPlus.setFrozen(frozen);
        service.frozen(userPlus);
        return BaseResponse.<Void>builder().get();
    }

    @PutMapping("/account/inner/recharge/{userId}")
    @Log(desc = "修改用户测试充值状态")
    public BaseResponse<Void> innerRecharge(@PathVariable("userId") Integer userId, @RequestParam("innerRecharge") boolean innerRecharge) {
        if (innerRecharge) {
            service.addRechargeLimit(userId, siteContext.getCurrentUser().getName());
        } else {
            service.delRechargeLimit(userId, siteContext.getCurrentUser().getName());
        }
        return BaseResponse.<Void>builder().get();
    }

    @PutMapping("/account/inner/recharge/white/{userId}")
    @Log(desc = "修改用户测试充值白名单状态")
    public BaseResponse<Void> innerRechargeWhite(@PathVariable("userId") Integer userId, @RequestParam("innerRecharge") boolean innerRecharge) {
        if (innerRecharge) {
            service.addWhiteRechargeLimit(userId, siteContext.getCurrentUser().getName());
        } else {
            service.delRechargeLimitWhite(userId, siteContext.getCurrentUser().getName());
        }
        return BaseResponse.<Void>builder().get();
    }

    @GetMapping("/account/inner/recharge/white/list")
    public BaseResponse<List<WhiteListResponse>> whiteList() {
        List<UserInnerRechargeLimit> limits = service.findWhiteList();
        if (CollectionUtils.isEmpty(limits)) {
            return BaseResponse.<List<WhiteListResponse>>builder().get();
        }
        List<Integer> ids = limits.stream().map(UserInnerRechargeLimit::getUserId).collect(Collectors.toList());
        List<UserPlus> userPluses = service.findByUserIds(ids);
        return BaseResponse.<List<WhiteListResponse>>builder().data(toWhiteListResponseList(limits, userPluses)).get();
    }

    @PutMapping("/account/recovery/{userId}")
    @Log(desc = "恢复用户")
    public BaseResponse<Void> recovery(@PathVariable("userId") Integer userId) {
        UserPlus userPlus = service.getUserPlus(userId);
        userPlus.setIsDelete(1);
        service.update(userPlus);
        return BaseResponse.<Void>builder().get();
    }

    private List<WhiteListResponse> toWhiteListResponseList(List<UserInnerRechargeLimit> limits, List<UserPlus> userPluses) {
        Map<Integer, UserPlus> userPlusMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(userPluses)) {
            userPlusMap = userPluses.stream().collect(Collectors.toMap(UserPlus::getId, Function.identity()));
        }
        List<WhiteListResponse> responses = new ArrayList<>();
        for (UserInnerRechargeLimit limit : limits) {
            WhiteListResponse response = new WhiteListResponse();
            response.setCreateBy(limit.getCreateBy());
            response.setCreateDate(limit.getCreateDate());
            response.setUserId(limit.getUserId());
            UserPlus user = userPlusMap.get(limit.getUserId());
            if (null != user) {
                response.setName(user.getName());
                response.setPhone(user.getUserName());
            }
            responses.add(response);
        }
        return responses;
    }


}
