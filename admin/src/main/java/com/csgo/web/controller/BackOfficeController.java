package com.csgo.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import com.csgo.web.support.LoginRequired;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.interceptor.session.RequireSession;


@RequireSession
@LoginRequired
public class BackOfficeController {

    @Autowired
    protected SiteContext siteContext;
}
