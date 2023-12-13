package com.csgo.web.controller.mine;

import com.csgo.service.mine.MineUserActivityService;
import com.csgo.support.GlobalConstants;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.request.mine.MineUserActivityLastDropDetailsRequest;
import com.csgo.web.request.mine.MineUserActivityPassLevelRequest;
import com.csgo.web.request.mine.MineUserConfigPriceRequest;
import com.csgo.web.response.mine.*;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api
@io.swagger.annotations.Api(tags = "扫雷玩法--活动管理")
@RestController
@RequestMapping("/mine/activity")
@Slf4j
@LoginRequired
@RequireSession
public class MineUserActivityController {

    @Autowired
    private MineUserActivityService mineUserActivityService;
    @Autowired
    private SiteContext siteContext;

    /**
     * 奖励价格区间设置
     *
     * @return
     */
    @ApiOperation("奖励价格区间设置")
    @PostMapping(value = "/priceConfig")
    public BaseResponse<Void> priceConfig(@RequestBody MineUserConfigPriceRequest request) {
        mineUserActivityService.priceConfig(siteContext.getCurrentUser().getId(),
                siteContext.getCurrentUser().getName(), request);
        return BaseResponse.<Void>builder().get();
    }

    /**
     * 活动奖励抽取
     *
     * @return
     */
    @ApiOperation("活动奖励抽取")
    @PostMapping(value = "/extractReward")
    public BaseResponse<MineUserActivityResponse> extractReward() {
        return BaseResponse.<MineUserActivityResponse>builder().data(mineUserActivityService.extractReward(siteContext.getCurrentUser().getId(),
                siteContext.getCurrentUser().getName())).get();
    }

    /**
     * 活动支付
     *
     * @return
     */
    @ApiOperation("活动支付")
    @PostMapping(value = "/pay")
    public BaseResponse<Void> pay() {
        int userId = siteContext.getCurrentUser().getId();
        mineUserActivityService.pay(userId);
        return BaseResponse.<Void>builder().get();
    }

    /**
     * 获取当前活动信息
     *
     * @return
     */
    @ApiOperation("获取当前活动信息")
    @GetMapping(value = "/getInfo")
    public BaseResponse<MineUserActivityResponse> getInfo() {
        int userId = siteContext.getCurrentUser().getId();
        return BaseResponse.<MineUserActivityResponse>builder().data(mineUserActivityService.getInfo(userId)).get();
    }


    /**
     * 获取放弃挑战奖励信息
     *
     * @return
     */
    @ApiOperation("获取放弃挑战奖励信息")
    @GetMapping(value = "/getGiveUpPrize")
    public BaseResponse<MineUserGiveUpPrizeResponse> getGiveUpPrize() {
        int userId = siteContext.getCurrentUser().getId();
        return BaseResponse.<MineUserGiveUpPrizeResponse>builder().data(mineUserActivityService.getGiveUpPrize(userId)).get();
    }

    /**
     * 放弃挑战
     *
     * @return
     */
    @ApiOperation("放弃挑战")
    @PostMapping(value = "/giveUp")
    public BaseResponse<MineUserResultResponse> giveUp() {
        int userId = siteContext.getCurrentUser().getId();
        String name = siteContext.getCurrentUser().getName();
        return BaseResponse.<MineUserResultResponse>builder().data(mineUserActivityService.giveUp(userId, name)).get();
    }


    /**
     * 活动闯关
     *
     * @return
     */
    @ApiOperation("活动闯关")
    @PostMapping(value = "/passLevel")
    public BaseResponse<MineUserResultResponse> passLevel(@RequestBody MineUserActivityPassLevelRequest request) {
        int userId = siteContext.getCurrentUser().getId();
        String name = siteContext.getCurrentUser().getName();
        //当前用户标记
        Integer flag = siteContext.getCurrentUser().getFlag();
        //是否测试账号
        boolean isTest = false;
        if (flag != null && flag.equals(GlobalConstants.INTERNAL_USER_FLAG)) {
            isTest = true;
        }
        return BaseResponse.<MineUserResultResponse>builder().data(mineUserActivityService.passLevel(userId, name, request, isTest)).get();
    }

    /**
     * 最近掉落列表
     *
     * @return
     */
    @ApiOperation("最近掉落列表")
    @GetMapping(value = "/findLastDropList")
    public BaseResponse<List<MineUserLastDropResponse>> findLastDropList() {
        return BaseResponse.<List<MineUserLastDropResponse>>builder().data(mineUserActivityService.findLastDropList()).get();
    }

    /**
     * 最近掉落详情
     *
     * @return
     */
    @ApiOperation("最近掉落详情")
    @PostMapping(value = "/findLastDropDetails")
    public BaseResponse<List<MineUserLastDropDetailsResponse>> findLastDropDetails(@Valid @RequestBody MineUserActivityLastDropDetailsRequest request) {
        return BaseResponse.<List<MineUserLastDropDetailsResponse>>builder().data(mineUserActivityService.findLastDropDetails(request)).get();
    }
}
