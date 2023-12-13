package com.csgo.web.controller.face;


import com.csgo.service.face.AliRealNameAuthService;
import com.csgo.service.user.UserPlatformRewardRecordService;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.request.face.AliRealNameAuthCheckRequest;
import com.csgo.web.request.face.AliRealNameAuthInfo;
import com.csgo.web.request.face.AliRealNameAuthRequest;
import com.csgo.web.request.face.AliRealNameCheckResponse;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.support.jackson.json.JSON;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * 支付宝实名认证
 */
@Slf4j
@Api
@io.swagger.annotations.Api(tags = "支付宝实名认证")
@RequestMapping("/ali/realName/auth")
public class AliRealNameAuthController {
    @Autowired
    private AliRealNameAuthService aliRealNameAuthService;
    @Autowired
    private SiteContext siteContext;

    @Autowired
    private UserPlatformRewardRecordService userPlatformRewardRecordService;

    /**
     * 获取实名认证地址
     *
     * @return
     */
    @ApiOperation("获取实名认证地址")
    @LoginRequired
    @RequireSession
    @PostMapping("/getH5Url")
    public BaseResponse<String> getH5Url(@Valid @RequestBody AliRealNameAuthRequest aliRealNameAuthRequest) {
        aliRealNameAuthRequest.setUserId(siteContext.getCurrentUser().getId());
        return BaseResponse.<String>builder().data(aliRealNameAuthService.getH5Url(aliRealNameAuthRequest, siteContext.getCurrentUser().getFlag())).get();
    }

    /**
     * 检查实名认证是否通过
     */
    @ApiOperation("检查实名认证是否通过-并返回奖励信息")
    @PostMapping("/check")
    public BaseResponse<AliRealNameCheckResponse> checkV2(@Valid @RequestBody AliRealNameAuthCheckRequest aliRealNameAuthRequest) {
        log.info("[实名认证] request:{}", JSON.toJSON(aliRealNameAuthRequest));
        AliRealNameCheckResponse response = aliRealNameAuthService.check(aliRealNameAuthRequest);
        return BaseResponse.<AliRealNameCheckResponse>builder().data(response).get();
    }

    /**
     * 检查实名认证是否通过
     *
     * @return
     */
    @ApiOperation("获取实名认证信息")
    @LoginRequired
    @RequireSession
    @GetMapping("/info")
    public BaseResponse<AliRealNameAuthInfo> info() {
        int userId = siteContext.getCurrentUser().getId();
        int flag = siteContext.getCurrentUser().getFlag();
        return BaseResponse.<AliRealNameAuthInfo>builder().data(aliRealNameAuthService.getUserInfo(userId, flag)).get();
    }
}
