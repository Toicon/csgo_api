package com.csgo.web.intecepter;

import com.csgo.constants.I18nConstant;
import com.csgo.framework.i18n.LocaleMessageSourceResolver;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.support.jackson.json.JSON;
import com.echo.framework.util.ControllerHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


/**
 * @author admin
 */
@Slf4j
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private SiteContext siteContext;
    @Autowired
    private LocaleMessageSourceResolver localeMessageSourceResolver;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            LoginRequired loginRequired = ControllerHelper.findMethodOrClassLevelAnnotation(handler, LoginRequired.class);
            boolean logged = siteContext.logged();
            if ((loginRequired != null && !logged) || (logged && siteContext.frozenUser())) {
                log.info("logged: {}, expired session id: {}", logged, siteContext.getSessionContext().getId());
                siteContext.logout();
                handleLoginValidateFailure(response);
                return false;
            }
        }
        return super.preHandle(request, response, handler);
    }

    private void handleLoginValidateFailure(HttpServletResponse response) throws IOException {
        response.setHeader("Accept", "application/json;charset=UTF-8");
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String message = localeMessageSourceResolver.getMessage(I18nConstant.LOGIN_REQUIRE);
        response.getWriter().write(JSON.toJSON(BaseResponse.builder().code(HttpServletResponse.SC_UNAUTHORIZED).message(message).get()));
        response.flushBuffer();
    }
}
