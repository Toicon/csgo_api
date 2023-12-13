package com.csgo.web.controller.face;

import com.csgo.service.face.TencentRealNameService;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.request.face.TencentRealNameCheckRequest;
import com.csgo.web.request.face.TencentRealNameCheckResponse;
import com.csgo.web.support.SiteContext;
import com.csgo.web.support.UserInfo;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author admin
 */
@Api
@io.swagger.annotations.Api(tags = "腾讯实名认证")
@RequestMapping("/tencent/auth")
@LoginRequired
@RequireSession
@RequiredArgsConstructor
public class TencentRealNameController {

    private final SiteContext siteContext;

    private final TencentRealNameService tencentRealNameService;

    @ApiOperation(value = "检查实名认证是否通过-并返回奖励信息", notes = "当其他接口需要实名认证而未认证时，将返回 code: 7000")
    @PostMapping("/id-card-verification")
    @LoginRequired
    @RequireSession
    public BaseResponse<TencentRealNameCheckResponse> idCardVerification(@Valid @RequestBody TencentRealNameCheckRequest tencentRealNameCheckRequest) {
        UserInfo currentUser = siteContext.getCurrentUser();
        TencentRealNameCheckResponse response = tencentRealNameService.idCardVerification(tencentRealNameCheckRequest, currentUser);
        return BaseResponse.<TencentRealNameCheckResponse>builder().data(response).get();
    }

}
