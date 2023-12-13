/*
 * Copyright 2002-2014 the original author or authors.
 * All rights reserved.
 */
package com.echo.framework.platform.interceptor.session;

import com.echo.framework.platform.App;
import com.echo.framework.platform.web.cookie.Cookie;
import com.echo.framework.platform.web.cookie.CookieContext;
import com.echo.framework.support.collection.Key;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * @author admin
 */
public class SessionIdCookieProcessor implements SessionIdProcessor {

    private Cookie.Brief<String> sessionIdCookie;
    private Cookie.Brief<String> secureSessionIdCookie;

    private final App.Session sessionSetting;

    @Autowired
    private CookieContext cookieContext;

    public SessionIdCookieProcessor(App.Session sessionSetting) {
        this.sessionSetting = sessionSetting;
    }

    @Override
    public void generateSessionIdKey() {
        sessionIdCookie = Cookie.BriefBuilder.builder(Key.ofString(getSessionKey())).path("/").sessionScope().build();
        secureSessionIdCookie = Cookie.BriefBuilder.builder(Key.ofString(getSecureSessionKey())).secure(true).path("/").sessionScope().httpOnly(false).build();
    }

    private String getSessionKey() {
        return Objects.toString(sessionSetting.getSessionKeyPrefix(), "") + "SessionId";
    }

    private String getSecureSessionKey() {
        return Objects.toString(sessionSetting.getSessionKeyPrefix(), "") + "SecureSessionId";
    }

    @Override
    public String get(boolean secure) {
        return cookieContext.getCookie(secure ? secureSessionIdCookie : sessionIdCookie);
    }

    @Override
    public void set(boolean secure, String sessionId) {
        if (secure) {
            cookieContext.setCookie(secureSessionIdCookie, sessionId);
            return;
        }
        cookieContext.setCookie(sessionIdCookie, sessionId);
    }

    @Override
    public void delete(boolean secure) {
        if (secure) {
            cookieContext.deleteCookie(secureSessionIdCookie);
            return;
        }
        cookieContext.deleteCookie(sessionIdCookie);
    }
}
