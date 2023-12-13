package com.csgo.web.controller.config;

import com.csgo.domain.plus.config.SystemConfig;
import com.csgo.service.config.SystemConfigService;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author admin
 */
@Api
@RequireSession
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping("/system/config")
    public BaseResponse<String> get(@RequestParam("key") String key) {
        SystemConfig systemConfig = systemConfigService.get(key);
        if (systemConfig == null) {
            return BaseResponse.<String>builder().get();
        }
        return BaseResponse.<String>builder().data(systemConfig.getValue()).get();
    }
}
