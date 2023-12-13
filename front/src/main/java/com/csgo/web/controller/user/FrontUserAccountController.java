package com.csgo.web.controller.user;

import com.csgo.modular.user.enums.UserStatusEnums;
import com.csgo.modular.user.logic.UserLogic;
import com.csgo.util.DateUtilsEx;
import com.csgo.util.IpUtils;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.support.SiteContext;
import com.csgo.web.support.UserInfo;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author admin
 */

@Slf4j
@Api
@io.swagger.annotations.Api(tags = "用户-账号")
@RequestMapping("/user/account")
@RequiredArgsConstructor
@LoginRequired
@RequireSession
public class FrontUserAccountController {

    private final SiteContext siteContext;

    private final UserLogic userLogic;

    @ApiOperation("注销账号")
    @PostMapping("/cancel")
    public BaseResponse<Void> cancel(HttpServletRequest request) {
        log.info("用户注销：账号>>{},ip地址>>{},端口号>>{},时间>>{}",
                siteContext.getCurrentUser().getUserName(), IpUtils.getIp(request), request.getRemotePort(), DateUtilsEx.getDateStr(new Date()));

        UserInfo currentUser = siteContext.getCurrentUser();

        userLogic.changeStatus(currentUser.getId(), UserStatusEnums.CANCEL);

        siteContext.logout();
        return BaseResponse.<Void>builder().get();
    }

}
