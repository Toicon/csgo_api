package com.csgo.framework.security;

import com.csgo.framework.constant.UserTypeEnums;
import com.csgo.framework.security.model.SecurityUser;
import com.echo.framework.platform.interceptor.session.SessionContext;
import com.echo.framework.support.collection.Key;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.applet.AppletContext;

/**
 * @author admin
 */
@Component
@RequiredArgsConstructor
public class SecurityContext {

    private static final Key<SecurityUser> USER_SECURITY = Key.of("user-security", SecurityUser.class);

    @Autowired(required = false)
    private SessionContext sessionContext;

    public SecurityUser getUser() {
        if (sessionContext == null) {
            return null;
        }
        return getSecurityUser();
    }

    private SecurityUser getSecurityUser() {
        try {
            return sessionContext.get(USER_SECURITY);
        } catch (BeanCreationException e) {
            return null;
        }
    }

    public void login(UserTypeEnums type, Integer userId, String name) {
        SecurityUser securityUser = new SecurityUser(type.getCode(), userId, name);
        sessionContext.set(USER_SECURITY, securityUser);
    }

    public boolean hasLogin() {
        return getUser() != null;
    }

}
