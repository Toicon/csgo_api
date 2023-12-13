package com.csgo.web.controller.user;


import com.csgo.autoconfigure.AliGreenProperty;
import com.csgo.constants.CommonBizCode;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.plus.user.UserSteamUpdateRecord;
import com.csgo.domain.user.User;
import com.csgo.framework.exception.BizClientException;
import com.csgo.modular.common.sms.logic.SmsLogic;
import com.csgo.modular.user.logic.UserLogic;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.service.user.UserService;
import com.csgo.service.user.UserSteamUpdateRecordService;
import com.csgo.sms.SmsService;
import com.csgo.support.BusinessException;
import com.csgo.support.ExceptionCode;
import com.csgo.support.StandardExceptionCode;
import com.csgo.util.IpUtils;
import com.csgo.util.ZcUtils;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.request.user.EditUserPasswordRequest;
import com.csgo.web.request.user.EditUserRequest;
import com.csgo.web.request.user.UpdateUserImageRequest;
import com.csgo.web.response.user.UserResponse;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;


@Api
@LoginRequired
@RequireSession
@RequestMapping("/user")
@Slf4j
public class UserController {

    private static final int MSG_SECOND = 60;

    @Autowired
    private UserService userService;
    @Autowired
    private SiteContext siteContext;
    @Autowired
    private AliGreenProperty aliGreenProperty;
    @Autowired
    private SmsLogic smsLogic;
    @Autowired
    private SmsService smsValidateService;
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;
    @Autowired
    private UserSteamUpdateRecordService userSteamUpdateRecordService;
    @Autowired
    private UserLogic userLogic;

    @PutMapping("/update-email")
    public BaseResponse<Void> updateEmail(@RequestParam(value = "email") String email) {
        Integer userId = siteContext.getCurrentUser().getId();
        userLogic.updateEmail(userId, email);
        return BaseResponse.<Void>builder().get();
    }

    @PutMapping("/update-image")
    public BaseResponse<Void> update(@Valid @RequestBody UpdateUserImageRequest request) {
        int id = siteContext.getCurrentUser().getId();
        User user = new User();
        if (!StringUtils.isEmpty(request.getImg())) {
            user.setImg(request.getImg());
        }
        user.setUpdatedAt(new Date());
        userService.update(user, id);
        return BaseResponse.<Void>builder().get();
    }

    @GetMapping("/get-steam-msg-code")
    public BaseResponse<Integer> getSteamMsgCode() {
        List<UserSteamUpdateRecord> records = userSteamUpdateRecordService.findByUserAndDate(siteContext.getCurrentUser().getId());
        if (!CollectionUtils.isEmpty(records)) {
            throw new BusinessException(ExceptionCode.STEAM_DAY_ONE);
        }
        UserPlus user = userService.get(siteContext.getCurrentUser().getId());
        if (!redisTemplateFacde.tryLock("Steam-Phone-" + user.getPhone(), MSG_SECOND)) {
            throw BizClientException.of(CommonBizCode.COMMON_BUSY);
        }
        String smsCode = RandomStringUtils.randomNumeric(6);
        boolean response = smsLogic.sendSmsCode(user.getPhone(), smsCode);
        if (response) {
            // redis存入缓存，并且5*60s过期
            String key = "sms:" + user.getPhone();
            redisTemplateFacde.set(key, smsCode, 5 * 60);
        }
        return BaseResponse.<Integer>builder().data(MSG_SECOND).get();
    }

    @PostMapping("/bind/phone")
    public BaseResponse<UserResponse> bindPhone(String phone, String password,
                                                String verificationCode) {
        if (StringUtils.isEmpty(phone)) {
            throw BizClientException.of(CommonBizCode.USER_MOBILE_BLANK);
        }
        if (StringUtils.isEmpty(password)) {
            throw BizClientException.of(CommonBizCode.USER_PASSWORD_BLANK);
        }
        if (StringUtils.isEmpty(verificationCode)) {
            throw BizClientException.of(CommonBizCode.USER_SMS_CODE_BLANK);
        }
        User list = userService.getUserByUserName(phone);
        if (list != null) {
            throw BizClientException.of(CommonBizCode.USER_BIND_MOBILE_EXIST);
        }
        boolean isValidSmsCode = smsValidateService.isValidSmsCode(phone, verificationCode);
        if (!isValidSmsCode) {
            throw BizClientException.of(CommonBizCode.USER_SMS_CODE_ERROR);
        }
        UserPlus user = userService.get(siteContext.getCurrentUser().getId());
        user.setPassword(password);
        user.setPhone(phone);
        user.setUserName(phone);
        user.setUpdatedAt(new Date());
        userService.update(user);
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return BaseResponse.<UserResponse>builder().data(response).get();
    }

    @PostMapping("/update/password")
    public BaseResponse<Void> updatePassword(@RequestBody EditUserPasswordRequest request) {
        if (StringUtils.isEmpty(request.getOldPassword())) {
            throw BizClientException.of(CommonBizCode.USER_OLD_PASSWORD_BLANK);
        }
        if (StringUtils.isEmpty(request.getPassword())) {
            throw BizClientException.of(CommonBizCode.USER_PASSWORD_BLANK);
        }
        Integer id = siteContext.getCurrentUser().getId();
        UserPlus user = userService.get(id);
        if (!user.getPassword().equals(request.getOldPassword())) {
            throw BizClientException.of(CommonBizCode.USER_OLD_PASSWORD_ERROR);
        }
        user.setPassword(request.getPassword());
        user.setUpdatedAt(new Date());
        userService.update(user);
        return BaseResponse.<Void>builder().get();
    }

    /**
     * 编辑用户
     *
     * @return
     */
    @ApiOperation("编辑用户（后台管理）")
    @PutMapping(value = "/update")
    public BaseResponse<Void> updateInfo(@Valid @RequestBody EditUserRequest request, HttpServletRequest servletRequest) {
        Integer userId = siteContext.getCurrentUser().getId();
        UserPlus user = userService.get(userId);
        if (!StringUtils.isEmpty(request.getSteam())) {
            validateSteamCode(request.getSmsCode(), user.getPhone(), user.getFlag(), user.getId());
        }
        if (!StringUtils.isEmpty(request.getName())) {
            //判断昵称是否合法
            try {
                boolean isOk = ZcUtils.checkText(aliGreenProperty.getAccessKey(), aliGreenProperty.getSecretKey(), request.getName());
                if (!isOk) {
                    throw BizClientException.of(CommonBizCode.USER_USERNAME_ILLEGAL);
                }
            } catch (Exception ex) {
                throw BizClientException.of(CommonBizCode.COMMON_SYSTEM_ERROR);
            }
        }
        userService.updateInfo(user, request, IpUtils.getIp(servletRequest));
        return BaseResponse.<Void>builder().get();
    }

    private void validateSteamCode(String smsCode, String phone, Integer flag, int userId) {
        if (1 == flag) {
            return;
        }
        if (!smsValidateService.isValidSmsCode(phone, smsCode)) {
            throw new BusinessException(ExceptionCode.MSG_CODE_ERROR);
        }
        smsValidateService.delSmsCode(phone);
    }

    @ApiOperation("判断修改用户昵称是否存在")
    @GetMapping("/checkNameExits")
    public BaseResponse<Boolean> checkNameExits() {
        Integer id = siteContext.getCurrentUser().getId();
        String name = siteContext.getCurrentUser().getName();
        return BaseResponse.<Boolean>builder().data(userService.checkNameExits(id, name)).get();
    }
}
