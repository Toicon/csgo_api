package com.csgo.web.controller.second;

import com.csgo.service.second.UserSecondRechargeRemindService;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.response.second.UserSecondRechargeRemindResponse;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;

@Api
@io.swagger.annotations.Api(tags = "二次充值提醒")
@LoginRequired
@RequireSession
@RequestMapping("/second/recharge")
public class SecondRechargeController {

    @Autowired
    private UserSecondRechargeRemindService userSecondRechargeRemindService;
    @Autowired
    private SiteContext siteContext;

    @GetMapping("/check")
    @ApiOperation("二次充值提醒校验")
    public BaseResponse<UserSecondRechargeRemindResponse> check() {
        Integer userId = siteContext.getCurrentUser().getId();
        return BaseResponse.<UserSecondRechargeRemindResponse>builder().data(userSecondRechargeRemindService.getSecondRechargeRemind(userId)).get();
    }

   
    @GetMapping("/getCouponPrice/{priceItemId}")
    @ApiOperation("获取优惠充值折扣金额")
    public BaseResponse<BigDecimal> getCouponPrice(@PathVariable(value = "priceItemId") int priceItemId) {
        Integer userId = siteContext.getCurrentUser().getId();
        return BaseResponse.<BigDecimal>builder().data(userSecondRechargeRemindService.getRechargeRemind(userId, priceItemId)).get();
    }
}
