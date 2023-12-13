package com.csgo.web.controller.fish;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.fish.SearchFishMyUserPrizeCondition;
import com.csgo.domain.plus.fish.FishUserPrize;
import com.csgo.service.fish.FishUserActivityService;
import com.csgo.support.DataConverter;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.request.fish.*;
import com.csgo.web.response.fish.*;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Api
@io.swagger.annotations.Api(tags = "钓鱼玩法--活动管理")
@RestController
@RequestMapping("/fish/activity")
@Slf4j
@LoginRequired
@RequireSession
public class FishUserActivityController {

    @Autowired
    private FishUserActivityService fishUserActivityService;
    @Autowired
    private SiteContext siteContext;

    /**
     * 获取钓鱼活动配置信息
     *
     * @return
     */
    @ApiOperation("获取钓鱼活动配置信息")
    @PostMapping(value = "/findConfigList")
    public BaseResponse<List<FishConfigResponse>> findConfigList(@Valid @RequestBody FishActivityConfigRequest request) {
        int userId = siteContext.getCurrentUser().getId();
        return BaseResponse.<List<FishConfigResponse>>builder().data(fishUserActivityService.findConfigList(request.getSessionType(), userId)).get();
    }

    /**
     * 获取鱼饵配置信息
     *
     * @return
     */
    @ApiOperation("获取鱼饵配置信息")
    @PostMapping(value = "/findBaitConfigList")
    public BaseResponse<List<FishBaitConfigResponse>> findBaitConfigList(@Valid @RequestBody FishActivityConfigRequest request) {
        return BaseResponse.<List<FishBaitConfigResponse>>builder().data(fishUserActivityService.findBaitConfigList(request.getSessionType())).get();
    }

    /**
     * 抛竿
     *
     * @return
     */
    @ApiOperation("抛竿")
    @PostMapping(value = "/hook")
    public BaseResponse<FishUserHookResponse> hook(@Valid @RequestBody FishActivityHookRequest request) {
        int userId = siteContext.getCurrentUser().getId();
        String name = siteContext.getCurrentUser().getName();
        return BaseResponse.<FishUserHookResponse>builder().data(fishUserActivityService.hook(userId, name, request)).get();
    }

    /**
     * 当前钓鱼信息
     *
     * @return
     */
    @ApiOperation("当前钓鱼信息")
    @PostMapping(value = "/getInfo")
    public BaseResponse<FishUserInfoResponse> getInfo(@Valid @RequestBody FishActivityInfoRequest request) {
        int userId = siteContext.getCurrentUser().getId();
        return BaseResponse.<FishUserInfoResponse>builder().data(fishUserActivityService.getInfo(userId, request.getGiftId())).get();
    }


    /**
     * 主动起竿
     *
     * @return
     */
    @ApiOperation("主动起竿")
    @PostMapping(value = "/giveUp")
    public BaseResponse<FishUserGiveUpResponse> giveUp(@Valid @RequestBody FishGiveUpRequest request) {
        int userId = siteContext.getCurrentUser().getId();
        return BaseResponse.<FishUserGiveUpResponse>builder().data(fishUserActivityService.giveUp(userId, request)).get();
    }

    /**
     * 一键起竿
     *
     * @return
     */
    @ApiOperation("一键起竿")
    @PostMapping(value = "/touchGiveUp")
    public BaseResponse<FishUserGiveUpResponse> touchGiveUp(@Valid @RequestBody FishTouchGiveUpRequest request) {
        int userId = siteContext.getCurrentUser().getId();
        String name = siteContext.getCurrentUser().getName();
        return BaseResponse.<FishUserGiveUpResponse>builder().data(fishUserActivityService.touchGiveUp(userId, name, request)).get();
    }


    /**
     * 定时起竿
     *
     * @return
     */
    @ApiOperation("定时起竿")
    @PostMapping(value = "/timeGiveUp")
    public BaseResponse<FishUserTimeGiveUpResponse> timeGiveUp(@Valid @RequestBody FishTimeGiveUpRequest request) {
        int userId = siteContext.getCurrentUser().getId();
        return BaseResponse.<FishUserTimeGiveUpResponse>builder().data(fishUserActivityService.timeGiveUp(userId, request.getGiftId(), request.getTurn())).get();
    }


    /**
     * 最近掉落
     *
     * @return
     */
    @ApiOperation("最近掉落")
    @PostMapping(value = "/findLastFall")
    public BaseResponse<List<FishUserPrizeFallResponse>> findLastFall(@Valid @RequestBody FishLastFallRequest request) {
        return BaseResponse.<List<FishUserPrizeFallResponse>>builder().data(fishUserActivityService.findLastFall(request.getGiftId())).get();
    }

    /**
     * 最近挂机奖励结果
     *
     * @return
     */
    @ApiOperation("最近挂机奖励结果")
    @PostMapping(value = "/findPrizeResult")
    public BaseResponse<List<FishUserPrizeResultResponse>> findPrizeResult(@Valid @RequestBody FishLastResultRequest request) {
        int userId = siteContext.getCurrentUser().getId();
        return BaseResponse.<List<FishUserPrizeResultResponse>>builder().data(fishUserActivityService.findPrizeResult(userId, request.getGiftId())).get();
    }


    /**
     * 我的网箱
     *
     * @return
     */
    @ApiOperation("我的网箱")
    @PostMapping(value = "/findMyUserPrize")
    public PageResponse<FishMyUserPrizeResponse> findMyUserPrize(@Valid @RequestBody FishMyUserPrizeRequest request) {
        SearchFishMyUserPrizeCondition condition = DataConverter.to(SearchFishMyUserPrizeCondition.class, request);
        int userId = siteContext.getCurrentUser().getId();
        condition.setUserId(userId);
        Page<FishUserPrize> pagination = fishUserActivityService.findMyUserPrize(condition);
        return DataConverter.to(item -> {
            FishMyUserPrizeResponse response = new FishMyUserPrizeResponse();
            response.setGiftProductName(item.getGiftProductName());
            response.setGiftProductImage(item.getGiftProductImage());
            response.setGiftProductPrice(item.getGiftProductPrice());
            response.setOutProbability(item.getOutProbability());
            return response;
        }, pagination);
    }
}
