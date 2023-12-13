package com.csgo.web.controller.fish;

import com.csgo.condition.fish.SearchFishPriceCondition;
import com.csgo.service.fish.FishPrizeService;
import com.csgo.support.DataConverter;
import com.csgo.support.Result;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.fish.FishPrizeQueryRequest;
import com.csgo.web.support.Log;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 钓鱼玩法--用户奖励管理
 */
@RestController
@RequestMapping("/fish/prize")
@Api(tags = "钓鱼玩法-用户奖励管理")
public class AdminFishPrizeController extends BackOfficeController {

    @Autowired
    private FishPrizeService fishPrizeService;

    @PostMapping("pageList")
    @Log(desc = "用户奖励查询")
    public Result pageList(@RequestBody FishPrizeQueryRequest request) {
        SearchFishPriceCondition condition = DataConverter.to(SearchFishPriceCondition.class, request);
        return new Result().result(fishPrizeService.pageList(condition));
    }


}
