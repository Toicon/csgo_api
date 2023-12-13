package com.csgo.web.controller.user;


import com.csgo.framework.util.RUtil;
import com.csgo.modular.backpack.model.admin.AdminGiftKeyUserMessageVM;
import com.csgo.modular.backpack.service.UserGiftKeyBackpackService;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.support.jackson.json.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 用户背包
 *
 * @author admin
 */
@Api
@Slf4j
@RestController
@RequestMapping("/user/backpack")
@RequiredArgsConstructor
public class AdminFrontUserBackpackController extends BackOfficeController {

    private final UserGiftKeyBackpackService userGiftKeyBackpackService;

    @Log(desc = "新增用户背包道具-钥匙")
    @PostMapping("/addGiftKey")
    public BaseResponse<Void> addGiftKey(@Valid @RequestBody AdminGiftKeyUserMessageVM vm) {
        log.info("[添加用户钥匙] user id:{} vm:{} opUserId:{}", vm.getUserId(), JSON.toJSON(vm), siteContext.getCurrentUser().getId());
        userGiftKeyBackpackService.addGiftKey(vm);
        return RUtil.ok();
    }

}
