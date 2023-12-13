package com.csgo.web.controller.shop;

import com.csgo.condition.shop.SearchShopOrderCondition;
import com.csgo.service.shop.ShopOrderService;
import com.csgo.support.DataConverter;
import com.csgo.util.BeanUtilsEx;
import com.csgo.util.DateUtils;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.shop.SearchShopOrderRequest;
import com.csgo.web.response.shop.ShopOrderResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;

@RestController
@RequestMapping("/shop/order")
@Api
public class AdminShopOrderController extends BackOfficeController {

    @Autowired
    private ShopOrderService shopOrderService;

    @PostMapping("/pageList")
    @Log(desc = "查看商品记录")
    public PageResponse<ShopOrderResponse> pageList(@Validated @RequestBody SearchShopOrderRequest request) {
        SearchShopOrderCondition condition = DataConverter.to(SearchShopOrderCondition.class, request);
        if (null != condition.getEndDate()) {
            condition.setEndDate(DateUtils.addDate(condition.getEndDate(), 1, Calendar.DATE));
        }
        return DataConverter.to(order -> {
            ShopOrderResponse response = new ShopOrderResponse();
            BeanUtilsEx.copyProperties(order, response);
            return response;
        }, shopOrderService.pagination(condition));
    }
}
