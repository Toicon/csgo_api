package com.csgo.web.controller.withdraw;

import com.csgo.service.user.BalanceSupportService;
import com.csgo.service.withdraw.WithdrawPropZbtService;
import com.csgo.util.BeanUtilsEx;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.request.withdraw.WithdrawPropRequest;
import com.csgo.web.response.withdraw.WithdrawPropRelateResponse;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Admin on 2021/5/22
 */
@Api
@LoginRequired
@RequireSession
@RequestMapping("/withdraw")
@Slf4j
public class WithdrawPropController {
    @Autowired
    private WithdrawPropZbtService withdrawPropService;
    @Autowired
    private SiteContext siteContext;
    @Autowired
    private BalanceSupportService balanceSupportService;

    /**
     * 提取饰品
     */
    @PostMapping("/prop")
    public BaseResponse<List<String>> withdraw(@Valid @RequestBody WithdrawPropRequest request) {
        return BaseResponse.<List<String>>builder().data(balanceSupportService.withdraw(siteContext.getCurrentUser().getId(), request)).get();
    }

    @GetMapping("/find/relate")
    public BaseResponse<List<WithdrawPropRelateResponse>> findRelate() {
        return BaseResponse.<List<WithdrawPropRelateResponse>>builder().data(withdrawPropService.findRelate(siteContext.getCurrentUser().getId()).stream().map(relate -> {
            WithdrawPropRelateResponse response = new WithdrawPropRelateResponse();
            BeanUtilsEx.copyProperties(relate, response);
            return response;
        }).collect(Collectors.toList())).get();
    }
}
