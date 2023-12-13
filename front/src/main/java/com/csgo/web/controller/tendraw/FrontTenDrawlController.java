package com.csgo.web.controller.tendraw;

import com.csgo.framework.util.RUtil;
import com.csgo.modular.tendraw.model.front.*;
import com.csgo.modular.tendraw.service.TenDrawService;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author admin
 */

@Slf4j
@Api
@io.swagger.annotations.Api(tags = "十连")
@RequestMapping("/ten-draw")
@RequiredArgsConstructor
public class FrontTenDrawlController {

    private final SiteContext siteContext;

    private final TenDrawService tenDrawService;

    @ApiOperation("获取配置")
    @GetMapping("/config")
    public BaseResponse<TenDrawConfigVO> getConfig() {
        return RUtil.ok(tenDrawService.getConfig());
    }

    @ApiOperation("未完成的游戏")
    @GetMapping("/unfinished-game")
    @LoginRequired
    @RequireSession
    public BaseResponse<LastTenDrawVO> getUnFinishGame() {
        Integer userId = siteContext.getCurrentUser().getId();
        LastTenDrawVO vo = tenDrawService.getUnFinishGame(userId);
        return RUtil.ok(vo);
    }

    @ApiOperation("生成/预览奖励")
    @PostMapping("/preview")
    @LoginRequired
    @RequireSession
    public BaseResponse<TenDrawPreviewVO> preview(@Valid @RequestBody TenDrawPreviewVM vm) {
        TenDrawPreviewVO vo = tenDrawService.preview(vm);
        return RUtil.ok(vo);
    }

    @ApiOperation("支付")
    @PostMapping("/pay")
    @LoginRequired
    @RequireSession
    public BaseResponse<Void> pay(@Valid @RequestBody TenDrawPayVM vm) {
        Integer userId = siteContext.getCurrentUser().getId();
        tenDrawService.pay(userId, vm);
        return RUtil.ok();
    }

    @ApiOperation("抽奖")
    @PostMapping("/play")
    @LoginRequired
    @RequireSession
    public BaseResponse<TenDrawGamePlayVO> play(@Valid @RequestBody TenDrawGamePlayVM vm) {
        Integer userId = siteContext.getCurrentUser().getId();
        TenDrawGamePlayVO vo = tenDrawService.play(userId, vm);
        return RUtil.ok(vo);
    }

    @ApiOperation("结束游戏")
    @PostMapping("/finish")
    @LoginRequired
    @RequireSession
    public BaseResponse<Void> finish() {
        Integer userId = siteContext.getCurrentUser().getId();
        tenDrawService.finish(userId);
        return RUtil.ok();
    }

}
