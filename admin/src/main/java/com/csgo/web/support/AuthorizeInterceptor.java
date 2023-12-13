package com.csgo.web.support;

import com.csgo.support.Result;
import com.echo.framework.support.jackson.json.JSON;
import com.echo.framework.util.ControllerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by admin on 2020/10/14
 */
public class AuthorizeInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private SiteContext siteContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            Permission annotation = ControllerHelper.findMethodOrClassLevelAnnotation(handler, Permission.class);
            if (null == annotation) {
                return true;
            }
            UserInfo userInfo = siteContext.getCurrentUser();
            if (null == userInfo || CollectionUtils.isEmpty(userInfo.getAuthorizes()) || !judgePermission(userInfo.getAuthorizes(), annotation.value())) {
                handleNoPermission(response);
                return false;
            }
        }
        return super.preHandle(request, response, handler);
    }

    private boolean judgePermission(List<String> codeList, String[] values) {
        for (String value : values) {
            for (String code : codeList) {
                if (code.equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void handleNoPermission(HttpServletResponse response) throws IOException {
        response.setHeader("Accept", "application/json;charset=UTF-8");
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(JSON.toJSON(new Result(HttpServletResponse.SC_FORBIDDEN, "No Permission", null)));
        response.flushBuffer();
    }
}
