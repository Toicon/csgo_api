package com.csgo.web.controller.fish;

import com.csgo.condition.fish.SearchFishBaitConfigCondition;
import com.csgo.domain.plus.fish.FishBaitConfig;
import com.csgo.service.fish.FishBaitConfigService;
import com.csgo.support.DataConverter;
import com.csgo.support.Result;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.fish.FishBaitConfigQueryRequest;
import com.csgo.web.support.Log;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 钓鱼玩法--鱼饵配置管理
 */
@RestController
@RequestMapping("/fish/bait")
@Api(tags = "钓鱼玩法-鱼饵配置管理")
public class AdminFishBaitController extends BackOfficeController {

    @Autowired
    private FishBaitConfigService fishBaitConfigService;

    @PostMapping("pageList")
    @Log(desc = "鱼饵配置查询")
    public Result pageList(@RequestBody FishBaitConfigQueryRequest request) {
        SearchFishBaitConfigCondition condition = DataConverter.to(SearchFishBaitConfigCondition.class, request);
        return new Result().result(fishBaitConfigService.pageList(condition));
    }

    /**
     * 新增
     */
    @PostMapping("add")
    @Log(desc = "新增鱼饵配置")
    public Result add(@Validated @RequestBody FishBaitConfig fishBaitConfig) {
        return new Result().result(fishBaitConfigService.insert(fishBaitConfig));
    }

    /**
     * 修改
     */
    @PostMapping("update")
    @Log(desc = "修改鱼饵配置")
    public Result edit(@Validated @RequestBody FishBaitConfig fishBaitConfig) {
        return new Result().result(fishBaitConfigService.update(fishBaitConfig));
    }

}
