package com.csgo.web.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.user.SearchUserCommissionLogCondition;
import com.csgo.constants.CommonBizCode;
import com.csgo.domain.plus.user.UserCommissionLogDTO;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.user.UserCommissionLog;
import com.csgo.framework.exception.BizClientException;
import com.csgo.service.user.UserCommissionLogService;
import com.csgo.service.user.UserService;
import com.csgo.support.DataConverter;
import com.csgo.support.StandardExceptionCode;
import com.csgo.util.DateUtils;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.request.user.CommissionCollectionRequest;
import com.csgo.web.request.user.SearchUserCommissionLogRequest;
import com.csgo.web.response.user.UserCommissionLogResponse;
import com.csgo.web.support.SiteContext;
import com.csgo.web.support.UserInfo;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Api
@LoginRequired
@RequireSession
@RequestMapping("/api/commission")
public class CommissionController {

    @Autowired
    private UserCommissionLogService userCommissionLogService;
    @Autowired
    private UserService userService;
    @Autowired
    private SiteContext siteContext;

    @PostMapping("/pagination")
    public PageResponse<UserCommissionLogResponse> pagination(@RequestBody @Valid SearchUserCommissionLogRequest request) {
        SearchUserCommissionLogCondition condition = DataConverter.to(SearchUserCommissionLogCondition.class, request);
        condition.setUserId(siteContext.getCurrentUser().getId());
        Page<UserCommissionLogDTO> pagination = userCommissionLogService.pagination(condition);
        return DataConverter.to(userCommissionLog -> {
            UserCommissionLogResponse response = new UserCommissionLogResponse();
            BeanUtils.copyProperties(userCommissionLog, response);
            return response;
        }, pagination);
    }


    @GetMapping("/detail")
    public BaseResponse<Map<String, Object>> getPromotionCenter() {
        Map<String, Object> map = userCommissionLogService.getPromotionCenter(siteContext.getCurrentUser().getId());
        return BaseResponse.<Map<String, Object>>builder().data(map).get();
    }


    @PostMapping("/collection")
    public BaseResponse<Void> commissionCollection(@RequestBody @Valid CommissionCollectionRequest request) {
        Date date = request.getDate();
        if (date.compareTo(new Date()) != -1) {
            throw new ApiException(StandardExceptionCode.COMMISSION_COLLECTION_FAILURE, "当达到领取条件");
        }
        String format;
        try {
            format = DateUtils.DATE_FORMAT_.format(date);
        } catch (Exception e) {
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }
        UserInfo user = siteContext.getCurrentUser();
        List<UserCommissionLog> list = userCommissionLogService.getUserCommissionAmount(user.getId(), format);
        if (CollectionUtils.isEmpty(list)) {
            throw new ApiException(StandardExceptionCode.COMMISSION_COLLECTION_FAILURE, "未查到可以领取的佣金");
        }
        BigDecimal reduce = list.stream().map(UserCommissionLog::getCommissionAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        // 改变状态
        UserPlus currentUser = userService.get(user.getId());
        userCommissionLogService.updateStatusByDateAndUserId(currentUser, reduce, format);
        return BaseResponse.<Void>builder().get();
    }

}
