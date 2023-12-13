package com.csgo.web.controller;

import com.csgo.framework.util.RUtil;
import com.csgo.modular.system.model.FrontSystemSettingVO;
import com.csgo.modular.system.service.SystemSettingService;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author admin
 */
@Slf4j
@Api
@io.swagger.annotations.Api(tags = "配置信息")
@RequestMapping("/config")
@RequiredArgsConstructor
public class FrontSettingController {

    private final SystemSettingService systemSettingService;

    /**
     * http://localhost:8080/config/get-config
     */
    @ApiOperation("获取配置信息")
    @GetMapping("/get-config")
    public BaseResponse<FrontSystemSettingVO> getConfig() {
        FrontSystemSettingVO vo = systemSettingService.getConfig();
        return RUtil.ok(vo);
    }

}
