package com.csgo.web.controller.geetest;

import com.csgo.framework.util.RUtil;
import com.csgo.modular.verify.model.BehaviorRegisterVO;
import com.csgo.modular.verify.model.BehaviorValidateVM;
import com.csgo.modular.verify.service.GeetestService;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 9829
 */

@Slf4j
@Api
@io.swagger.annotations.Api(tags = "行为验证")
@RequestMapping("/behavior")
@RequiredArgsConstructor
public class FrontBehaviorController {


    private final GeetestService geetestService;

    @ApiOperation("验证初始化")
    @GetMapping("/register")
    public BaseResponse<BehaviorRegisterVO> register() {
        BehaviorRegisterVO vo = geetestService.register();
        return RUtil.ok(vo);
    }

    @ApiOperation("二次验证")
    @PostMapping("/validate")
    public BaseResponse<Void> validate(BehaviorValidateVM vm) {
        geetestService.validate(vm);
        return RUtil.ok();
    }

}
