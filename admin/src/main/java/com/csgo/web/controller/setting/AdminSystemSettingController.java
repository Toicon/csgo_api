package com.csgo.web.controller.setting;

import com.csgo.framework.util.RUtil;
import com.csgo.modular.system.domain.SystemSettingDO;
import com.csgo.modular.system.model.admin.SystemSettingUpdateByKeyVM;
import com.csgo.modular.system.service.SystemSettingService;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@Api
@RequestMapping("/system/setting")
@RequiredArgsConstructor
public class AdminSystemSettingController extends BackOfficeController {

    private final SystemSettingService systemSettingService;

    @PutMapping("/update-by-key")
    @Log(desc = "修改Setting配置-通过Key")
    public BaseResponse<SystemSettingDO> updateSettingByKey(@Valid @RequestBody SystemSettingUpdateByKeyVM vm) {
        SystemSettingDO entity = systemSettingService.updateSettingByKey(vm);
        return RUtil.ok(entity);
    }

    @GetMapping("/get-by-key")
    public BaseResponse<SystemSettingDO> getSettingByKey(@RequestParam String configKey) {
        SystemSettingDO entity = systemSettingService.getSettingByKey(configKey);
        return RUtil.ok(entity);
    }

}
