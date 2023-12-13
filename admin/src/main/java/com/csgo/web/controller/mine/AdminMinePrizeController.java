package com.csgo.web.controller.mine;

import com.csgo.condition.mine.SearchMinePriceCondition;
import com.csgo.domain.plus.mine.MineUserActivityPassLevel;
import com.csgo.mapper.plus.mine.MineUserActivityPassLevelMapper;
import com.csgo.service.mine.MinePrizeService;
import com.csgo.support.DataConverter;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.mine.MinePrizeQueryRequest;
import com.csgo.web.response.mine.MinePrizeResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.response.PageResponse;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 扫雷活动--用户奖励管理
 */
@RestController
@RequestMapping("/mine/prize")
@Api(tags = "扫雷活动-用户奖励管理")
public class AdminMinePrizeController extends BackOfficeController {

    @Autowired
    private MinePrizeService minePrizeService;
    @Autowired
    private MineUserActivityPassLevelMapper mineUserActivityPassLevelMapper;

    @PostMapping("pageList")
    @Log(desc = "用户奖励查询")
    public PageResponse<MinePrizeResponse> pageList(@RequestBody MinePrizeQueryRequest request) {
        SearchMinePriceCondition condition = DataConverter.to(SearchMinePriceCondition.class, request);
        return DataConverter.to(minePrize -> {
            MinePrizeResponse response = new MinePrizeResponse();
            Integer activityId = minePrize.getActivityId();
            MineUserActivityPassLevel mineUserActivityPassLevel = mineUserActivityPassLevelMapper.getLastDrop(activityId);
            if (mineUserActivityPassLevel != null) {
                response.setLevel(mineUserActivityPassLevel.getLevel());
                response.setPassState(mineUserActivityPassLevel.getPassState());
            }
            response.setActivityId(activityId);
            response.setGiftProductId(minePrize.getGiftProductId());
            response.setPayPrice(minePrize.getPayPrice());
            response.setPrizeName(minePrize.getPrizeName());
            response.setPrizePrice(minePrize.getPrizePrice());
            response.setUserId(minePrize.getUserId());
            response.setUserName(minePrize.getUserName());
            response.setCreateDate(minePrize.getCreateDate());
            return response;
        }, minePrizeService.pageList(condition));

    }


}
