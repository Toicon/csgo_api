package com.csgo.web.controller.steam;

import com.csgo.service.steam.SteamValidateService;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Admin on 2021/6/8
 */
@Api
@RequestMapping("/steam")
public class SteamController {
    @Autowired
    private SteamValidateService steamValidateService;

    @GetMapping("/trade")
    public BaseResponse<String> trade() {
        return BaseResponse.<String>builder().data(steamValidateService.loginUrl()).get();
    }
}
