package com.csgo.web.support;

import com.csgo.domain.plus.role.RoleAuthorize;
import com.csgo.domain.plus.user.AdminUserPlus;
import com.csgo.framework.constant.UserTypeEnums;
import com.csgo.framework.security.SecurityContext;
import com.csgo.service.RoleAuthorizeService;
import com.echo.framework.platform.interceptor.session.SessionContext;
import com.echo.framework.support.collection.Key;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class SiteContext {

    private static final Key<UserInfo> USER_INFO = Key.of("user-info", UserInfo.class);

    @Autowired
    private SecurityContext securityContext;
    @Autowired
    private SessionContext sessionContext;
    @Autowired
    private RoleAuthorizeService roleAuthorizeService;

    public void login(AdminUserPlus user) {
        UserInfo adminUserInfo = new UserInfo();
        BeanUtils.copyProperties(user, adminUserInfo);
        RoleAuthorize roleAuthorize = new RoleAuthorize();
        roleAuthorize.setRoleId(user.getRoleId());
        List<RoleAuthorize> roleAuthorizes = roleAuthorizeService.find(user.getRoleId(), null);
        if (!CollectionUtils.isEmpty(roleAuthorizes)) {
            adminUserInfo.setAuthorizes(roleAuthorizes.stream().map(RoleAuthorize::getCode).collect(Collectors.toList()));
        }
        sessionContext.set(USER_INFO, adminUserInfo);

        securityContext.login(UserTypeEnums.ADMIN, user.getId(), user.getName());
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
}
