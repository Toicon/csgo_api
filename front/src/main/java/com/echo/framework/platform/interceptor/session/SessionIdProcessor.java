/*
 * Copyright 2002-2014 the original author or authors.
 * All rights reserved.
 */
package com.echo.framework.platform.interceptor.session;

/**
 * @author admin
 */
public interface SessionIdProcessor {

    void generateSessionIdKey();

    String get(boolean secure);

    void set(boolean secure, String sessionId);

    void delete(boolean secure);
}
