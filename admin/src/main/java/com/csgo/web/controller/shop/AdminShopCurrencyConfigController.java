package com.csgo.web.controller.shop;

import com.csgo.condition.shop.SearchShopCurrencyConfigCondition;
import com.csgo.service.shop.ShopCurrencyConfigService;
import com.csgo.support.DataConverter;
import com.csgo.support.Result;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.shop.AddShopCurrencyConfigRequest;
import com.csgo.web.request.shop.DeleteShopCurrencyConfigRequest;
import com.csgo.web.request.shop.ShopCurrencyConfigQueryRequest;
import com.csgo.web.request.shop.UpdateShopCurrencyConfigRequest;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.response.BaseResponse;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 商城货币兑换配置
 */
@RestController
@RequestMapping("/shop/currency")
@Api(tags = "商城货币兑换配置")
public class AdminShopCurrencyConfigController extends BackOfficeController {

    @Autowired
    private ShopCurrencyConfigService shopCurrencyConfigService;

    @PostMapping("/pageList")
    @Log(desc = "商城货币兑换查询")
    public Result pageList(@RequestBody ShopCurrencyConfigQueryRequest request) {
        SearchShopCurrencyConfigCondition condition = DataConverter.to(SearchShopCurrencyConfigCondition.class, request);
        return new Result().result(shopCurrencyConfigService.pageList(condition));
    }

    @PostMapping("/add")
    @Log(desc = "添加商城货币配置")
    public BaseResponse<Void> add(@Valid @RequestBody AddShopCurrencyConfigRequest req) {
        shopCurrencyConfigService.add(req.getDiamondAmount(), req.getGiveRate());
        return BaseResponse.<Void>builder().get();
    }

    @PostMapping("/update")
    @Log(desc = "添加商城货币配置")
    public BaseResponse<Void> update(@Valid @RequestBody UpdateShopCurrencyConfigRequest req) {
        shopCurrencyConfigService.update(req.getId(), req.getDiamondAmount(), req.getGiveRate());
        return BaseResponse.<Void>builder().get();
    }

    @PostMapping("/delete")
    @Log(desc = "删除商城货币配置")
    public BaseResponse<Void> delete(@Valid @RequestBody DeleteShopCurrencyConfigRequest req) {
        shopCurrencyConfigService.delete(req.getId());
        return BaseResponse.<Void>builder().get();
    }
}
