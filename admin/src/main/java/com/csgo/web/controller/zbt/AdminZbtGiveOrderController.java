package com.csgo.web.controller.zbt;

import com.csgo.condition.zbt.SearchZbtGiveOrderCondition;
import com.csgo.config.ZBTProperties;
import com.csgo.domain.plus.zbt.ZbtGiveOrderType;
import com.csgo.service.zbt.ZbtGiveOrderService;
import com.csgo.support.DataConverter;
import com.csgo.support.ZBT.ZBTResult;
import com.csgo.util.BeanUtilsEx;
import com.csgo.util.EmojiConverterUtil;
import com.csgo.util.HttpUtil2;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.zbt.SearchZbtGiveOrderRequest;
import com.csgo.web.request.zbt.SearchZbtMarketRequest;
import com.csgo.web.request.zbt.ZbtGiveOrderRequest;
import com.csgo.web.response.zbt.ZbtGiveOrderResponse;
import com.csgo.web.response.zbt.ZbtMarketResponse;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;
import com.echo.framework.support.jackson.json.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 2021/7/19
 */
@Api
@RequestMapping("/zbt")
public class AdminZbtGiveOrderController extends BackOfficeController {
    @Autowired
    private ZbtGiveOrderService zbtGiveOrderService;
    @Autowired
    private ZBTProperties properties;

    @PostMapping("/market/pagination")
    public PageResponse<ZbtMarketResponse> pagination(@Validated @RequestBody SearchZbtMarketRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("app-key", properties.getAppKey());
        map.put("appId", properties.getAppId());
        map.put("limit", request.getPageSize());
        map.put("page", request.getPageIndex());
        map.put("keyword", request.getKeyword());
        map.put("orderBy", 0);
        String string = HttpUtil2.doGet(properties.getSearchUrl(), map);
        ZBTResult result = JSON.fromJSON(string, ZBTResult.class);
        if (result.getSuccess() == null || !result.getSuccess() || null == result.getData() || CollectionUtils.isEmpty(result.getData().getList())) {
            return new PageResponse<>();
        }
        PageResponse<ZbtMarketResponse> pageResponse = new PageResponse<>();
        PageResponse.PageResult<ZbtMarketResponse> pageResult = new PageResponse.PageResult<>();
        result.getData().getList().forEach(item -> {
            ZbtMarketResponse response = new ZbtMarketResponse();
            BeanUtilsEx.copyProperties(item.getPriceInfo(), response);
            response.setGiftProductName(item.getItemName());
            response.setItemId(item.getItemId());
            pageResult.getRows().add(response);
        });
        pageResult.setTotal(result.getData().getTotal());
        pageResponse.setData(pageResult);
        return pageResponse;
    }

    @PostMapping("/give/order")
    public BaseResponse<Void> add(@Validated @RequestBody ZbtGiveOrderRequest request) {
        ZbtGiveOrderType type = ZbtGiveOrderType.valueOf(request.getDelivery());
        BigDecimal price = type.equals(ZbtGiveOrderType.AUTO) ? request.getAutoDeliverPrice() : request.getManualDeliverPrice();
        zbtGiveOrderService.insert(request.getGiftProductName(), request.getItemId(), request.getSteamUrl(), price, type, siteContext.getCurrentUser().getName());
        return BaseResponse.<Void>builder().get();
    }

    @PostMapping("/give/order/pagination")
    public PageResponse<ZbtGiveOrderResponse> search(@Validated @RequestBody SearchZbtGiveOrderRequest request) {
        SearchZbtGiveOrderCondition condition = DataConverter.to(SearchZbtGiveOrderCondition.class, request);
        return DataConverter.to(order -> {
            ZbtGiveOrderResponse response = new ZbtGiveOrderResponse();
            BeanUtilsEx.copyProperties(order, response);
            response.setNickName(EmojiConverterUtil.emojiRecovery(order.getNickName()));
            response.setType(order.getType().getDescription());
            response.setStatus(order.getStatus().getDescription());
            return response;
        }, zbtGiveOrderService.pagination(condition));
    }
}
