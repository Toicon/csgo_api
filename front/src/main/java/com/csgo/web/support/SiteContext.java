package com.csgo.web.support;

import com.csgo.framework.constant.UserTypeEnums;
import com.csgo.framework.security.SecurityContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.csgo.redis.RedisTemplateFacde;
import com.csgo.service.user.UserService;
import com.echo.framework.platform.interceptor.session.SessionContext;
import com.echo.framework.platform.interceptor.session.provider.RedisSessionProvider;
import com.echo.framework.support.collection.Key;


public class SiteContext {

    private static final Key<UserInfo> USER_INFO = Key.of("user-info", UserInfo.class);
    private static final String USER_REDIS_SESSION_ID_PREFIX = "user-session-id:";

    @Autowired
    private SecurityContext securityContext;
    @Autowired
    private SessionContext sessionContext;
    @Autowired
    private RedisTemplateFacde redisTemplate;
    @Autowired
    private RedisSessionProvider sessionProvider;
    @Autowired
    private UserService userService;

    public SessionContext getSessionContext() {
        return sessionContext;
    }

    public void login(UserInfo user) {
        String key = USER_REDIS_SESSION_ID_PREFIX + user.getId();
        String existsSessionId = redisTemplate.get(key);
        if (!StringUtils.isEmpty(existsSessionId)) {
            sessionProvider.clearSession(existsSessionId);
            redisTemplate.delete(existsSessionId);
        }
        sessionContext.set(USER_INFO, user);
        redisTemplate.set(key, sessionContext.getId());
        securityContext.login(UserTypeEnums.FRONT, user.getId(), user.getName());
    }

    public UserInfo getCurrentUser() {
        return sessionContext.get(USER_INFO);
    }

    public void logout() {
        sessionContext.set(USER_INFO, null);
    }


    public boolean logged() {
        return null != sessionContext.get(USER_INFO);
    }

    public boolean frozenUser() {
        UserInfo userInfo = sessionContext.get(USER_INFO);
        return userService.frozen(userInfo.getId());
    }
}
