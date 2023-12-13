package com.csgo.web.controller.user;

import java.util.Date;
import javax.validation.Valid;

import com.csgo.constants.CommonBizCode;
import com.csgo.framework.exception.BizServerException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.csgo.condition.user.SearchUserLuckyRecordCondition;
import com.csgo.condition.user.SearchUserRechargeRecordCondition;
import com.csgo.domain.plus.user.LuckyRecord;
import com.csgo.domain.plus.user.LuckyRecordType;
import com.csgo.domain.plus.user.Tag;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.service.UserService;
import com.csgo.service.recharge.RechargeService;
import com.csgo.service.user.LuckyRecordService;
import com.csgo.service.user.RechargeRecordService;
import com.csgo.support.DataConverter;
import com.csgo.support.GlobalConstants;
import com.csgo.support.StandardExceptionCode;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.user.InsertLuckyRecordRequest;
import com.csgo.web.request.user.InsertRechargeRecordRequest;
import com.csgo.web.request.user.SearchUserLuckyRecordRequest;
import com.csgo.web.request.user.SearchUserRechargeRecordRequest;
import com.csgo.web.response.user.UserLuckyRecordResponse;
import com.csgo.web.response.user.UserRechargeRecordResponse;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;

/**
 * @author admin
 */
@Api
@RequestMapping("/user/record")
public class AdminFrontUserRecordController extends BackOfficeController {

    private static final int OPERATING_USER_FLAG = 2;

    @Autowired
    private LuckyRecordService luckyRecordService;
    @Autowired
    private RechargeRecordService rechargeRecordService;
    @Autowired
    private UserService userService;
    @Autowired
    private RechargeService rechargeService;

    @PostMapping("/recharge/pagination")
    public PageResponse<UserRechargeRecordResponse> rechargePagination(@Valid @RequestBody SearchUserRechargeRecordRequest request) {
        SearchUserRechargeRecordCondition condition = DataConverter.to(SearchUserRechargeRecordCondition.class, request);
        if (null != request.getFlag()) {
            if (OPERATING_USER_FLAG == request.getFlag()) {
                condition.setFlag(GlobalConstants.INTERNAL_USER_FLAG);
                condition.setTag(Tag.OPERATOR);
            }
            if (GlobalConstants.INTERNAL_USER_FLAG == request.getFlag()) {
                condition.setTag(Tag.ANCHOR);
            }
        }
        return DataConverter.to(rechargeRecordDTO -> {
            UserRechargeRecordResponse response = new UserRechargeRecordResponse();
            BeanUtils.copyProperties(rechargeRecordDTO, response);
            if (Tag.OPERATOR.equals(rechargeRecordDTO.getTag())) {
                response.setFlag(OPERATING_USER_FLAG);
            }
            return response;
        }, rechargeRecordService.pagination(condition));
    }

    @PostMapping("/recharge")
    public BaseResponse<Void> rechargeInsert(@Valid @RequestBody InsertRechargeRecordRequest request) {
        UserPlus user = userService.getUserPlus(request.getUserId());
        if (user == null) {
            throw BizServerException.of(CommonBizCode.USER_NOT_FOUND);
        }
        rechargeService.recharge(user, request, siteContext.getCurrentUser().getName());
        return BaseResponse.<Void>builder().get();
    }

    @PostMapping("/lucky/pagination")
    public PageResponse<UserLuckyRecordResponse> luckyPagination(@Valid @RequestBody SearchUserLuckyRecordRequest request) {
        SearchUserLuckyRecordCondition condition = DataConverter.to(SearchUserLuckyRecordCondition.class, request);
        if (null != request.getFlag()) {
            if (OPERATING_USER_FLAG == request.getFlag()) {
                condition.setFlag(GlobalConstants.INTERNAL_USER_FLAG);
                condition.setTag(Tag.OPERATOR);
            }
            if (GlobalConstants.INTERNAL_USER_FLAG == request.getFlag()) {
                condition.setTag(Tag.ANCHOR);
            }
        }
        return DataConverter.to(userLuckyRecordDTO -> {
            UserLuckyRecordResponse response = new UserLuckyRecordResponse();
            BeanUtils.copyProperties(userLuckyRecordDTO, response);
            response.setType(userLuckyRecordDTO.getType().getDis());
            if (Tag.OPERATOR.equals(userLuckyRecordDTO.getTag())) {
                response.setFlag(OPERATING_USER_FLAG);
            }
            return response;
        }, luckyRecordService.pagination(condition));
    }

    @PostMapping("/lucky")
    public BaseResponse<Void> luckyInsert(@Valid @RequestBody InsertLuckyRecordRequest request) {
        UserPlus user = userService.getUserPlus(request.getUserId());
        LuckyRecord luckyRecord = new LuckyRecord();
        LuckyRecordType type = LuckyRecordType.valueOf(request.getType());
        luckyRecord.setType(type);
        if (LuckyRecordType.HOME.equals(type)) {
            luckyRecord.setOldLucky(user.getLucky());
            user.setLucky(request.getLucky());
        }
        if (LuckyRecordType.ACCESSORY.equals(type)) {
            luckyRecord.setOldLucky(user.getAccessoryLucky());
            user.setAccessoryLucky(request.getLucky());
        }
        luckyRecord.setUserId(request.getUserId());
        luckyRecord.setNewLucky(request.getLucky());
        luckyRecord.setCb(siteContext.getCurrentUser().getName());
        luckyRecord.setCt(new Date());
        userService.update(user);
        luckyRecordService.insert(luckyRecord);
        return BaseResponse.<Void>builder().get();
    }
}
