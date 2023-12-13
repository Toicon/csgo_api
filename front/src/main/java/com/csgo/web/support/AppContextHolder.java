/*
 * Copyright 2002-2014 the original author or authors.
 * All rights reserved.
 */
package com.csgo.web.support;

import org.springframework.core.NamedThreadLocal;

/**
 * @author admin
 */
public class AppContextHolder {

    private static final AppContextHolder INSTANCE = new AppContextHolder();
    private static final ThreadLocal<AppContext> CONTEXT_HOLDER = new NamedThreadLocal<>("thread-local-contexts");

    public static AppContextHolder get() {
        return INSTANCE;
    }

    public AppContext getContext() {
        return CONTEXT_HOLDER.get();
    }

    public void setAppContext(AppContext appContext) {
        CONTEXT_HOLDER.set(appContext);
    }

    public void clean() {
        CONTEXT_HOLDER.remove();
    }
}
