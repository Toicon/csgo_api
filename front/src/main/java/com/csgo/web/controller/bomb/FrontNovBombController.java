package com.csgo.web.controller.bomb;

import com.csgo.framework.util.RUtil;
import com.csgo.modular.bomb.model.front.*;
import com.csgo.modular.bomb.service.*;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.support.SiteContext;
import com.csgo.web.support.UserInfo;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author admin
 */
@Slf4j
@Api
@io.swagger.annotations.Api(tags = "模拟拆弹")
@RequestMapping("/bomb")
@RequiredArgsConstructor
public class FrontNovBombController {

    private final SiteContext siteContext;

    private final NovBombPreviewService novBombPreviewService;
    private final NovBombConfigService novBombConfigService;
    private final NovBombGameService novBombGameService;
    private final NovBombPayService novBombPayService;
    private final NovBombDrawService novBombDrawService;
    private final NovBombRewardService novBombRewardService;

    @ApiOperation("获取场次列表")
    @GetMapping("/config")
    public BaseResponse<NovBombHomeVO> getConfig() {
        return RUtil.ok(novBombConfigService.getConfig());
    }

    @ApiOperation("预览")
    @PostMapping("/config/{configId}/preview")
    public BaseResponse<NovBombPreviewVO> preview(@PathVariable Integer configId) {
        NovBombPreviewVO vo = novBombPreviewService.preview(configId);
        return RUtil.ok(vo);
    }

    @ApiOperation("支付")
    @PostMapping("/pay")
    @LoginRequired
    @RequireSession
    public BaseResponse<Void> pay(@Valid @RequestBody NovBombPayVM vm) {
        Integer userId = siteContext.getCurrentUser().getId();
        novBombPayService.pay(userId, vm);
        return RUtil.ok();
    }

    @ApiOperation("再次支付")
    @PostMapping("/pay-again")
    @LoginRequired
    @RequireSession
    public BaseResponse<Void> payAgain(@Valid @RequestBody NovBombPayAgainVM vm) {
        Integer userId = siteContext.getCurrentUser().getId();
        novBombPayService.payAgain(userId, vm);
        return RUtil.ok();
    }

    @ApiOperation("抽奖")
    @PostMapping("/play")
    @LoginRequired
    @RequireSession
    public BaseResponse<NovBombGamePlayVO> play(@Valid @RequestBody NovBombGamePlayVM vm) {
        Integer userId = siteContext.getCurrentUser().getId();
        NovBombGamePlayVO vo = novBombDrawService.draw(userId, vm);
        return RUtil.ok(vo);
    }

    @ApiOperation("结束游戏")
    @PostMapping("/finish")
    @LoginRequired
    @RequireSession
    public BaseResponse<Void> finish() {
        Integer userId = siteContext.getCurrentUser().getId();
        novBombGameService.finish(userId);
        return RUtil.ok();
    }

    @ApiOperation("未完成的游戏")
    @GetMapping("/unfinished-game")
    @LoginRequired
    @RequireSession
    public BaseResponse<LastNovBombVO> getUnFinishGame() {
        Integer userId = siteContext.getCurrentUser().getId();
        LastNovBombVO vo = novBombGameService.getUnFinishGameVo(userId);
        return RUtil.ok(vo);
    }

    @ApiOperation("排行榜")
    @GetMapping("/leaderboard")
    public BaseResponse<List<NovBombRewardVO>> getLeaderboard(
            @RequestParam(defaultValue = "50") Integer num,
            @RequestParam(required = false) Integer configId
    ) {
        List<NovBombRewardVO> voList = novBombRewardService.getLeaderboard(num, configId);
        return RUtil.ok(voList);
    }

    @ApiOperation("最近掉落")
    @GetMapping("/recent")
    public BaseResponse<List<NovBombRewardVO>> getRecent(
            @RequestParam(defaultValue = "50") Integer num,
            @RequestParam(required = false) Integer configId
    ) {
        List<NovBombRewardVO> voList = novBombRewardService.getRecent(num, configId);
        return RUtil.ok(voList);
    }

    @ApiOperation("未结束的历史记录")
    @GetMapping("/unfinished-history")
    @RequireSession
    public BaseResponse<List<NovBombRewardVO>> getUnFinishHistory(@RequestParam(defaultValue = "10") Integer num) {
        UserInfo currentUser = siteContext.getCurrentUser();
        if (currentUser == null) {
            return RUtil.ok(Lists.newArrayList());
        }
        List<NovBombRewardVO> voList = novBombRewardService.getUnFinishHistory(num, currentUser.getId());
        return RUtil.ok(voList);
    }

}
