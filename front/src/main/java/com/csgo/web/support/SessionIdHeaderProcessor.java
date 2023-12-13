/*
 * Copyright 2002-2014 the original author or authors.
 * All rights reserved.
 */
package com.csgo.web.support;

import com.echo.framework.platform.App;
import com.echo.framework.platform.interceptor.session.SessionIdCookieProcessor;

import javax.servlet.http.HttpServletRequest;

/**
 * @author admin
 */
public class SessionIdHeaderProcessor extends SessionIdCookieProcessor {

    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String APP_HEADER_KEY = "App";

    public SessionIdHeaderProcessor(App.Session sessionSetting) {
        super(sessionSetting);
    }

    @Override
    public String get(boolean secure) {
        return isFromApp() ? authorization() : super.get(secure);
    }

    @Override
    public void set(boolean secure, String sessionId) {
        if (isFromApp()) {
            AppContextHolder.get().getContext().getResponse().addHeader(AUTHORIZATION_HEADER_KEY, sessionId);
            return;
        }
        super.set(secure, sessionId);
    }

    @Override
    public void delete(boolean secure) {
        if (isFromApp()) {
            AppContextHolder.get().getContext().getResponse().addHeader(AUTHORIZATION_HEADER_KEY, null);
            return;
        }
        super.delete(secure);
    }

    private boolean isFromApp() {
        HttpServletRequest request = AppContextHolder.get().getContext().getRequest();
        return Boolean.parseBoolean(request.getHeader(APP_HEADER_KEY));
    }

    private String authorization() {
        HttpServletRequest request = AppContextHolder.get().getContext().getRequest();
        return request.getHeader(AUTHORIZATION_HEADER_KEY);
    }
}
