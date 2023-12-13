package com.csgo.web.controller.shop;

import com.csgo.condition.shop.SearchProductCondition;
import com.csgo.condition.shop.SearchShopCondition;
import com.csgo.domain.plus.shop.Shop;
import com.csgo.domain.plus.shop.ShopDTO;
import com.csgo.service.ExchangeRateService;
import com.csgo.service.GiftProductService;
import com.csgo.service.ZbtProductFiltersService;
import com.csgo.service.shop.ShopService;
import com.csgo.service.shop.ShopStockService;
import com.csgo.support.BusinessException;
import com.csgo.support.DataConverter;
import com.csgo.support.ExceptionCode;
import com.csgo.util.BeanUtilsEx;
import com.csgo.web.controller.BackOfficeController;
import com.csgo.web.request.shop.*;
import com.csgo.web.response.product.GiftProductResponse;
import com.csgo.web.response.shop.ShopResponse;
import com.csgo.web.support.Log;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/shop")
@Api
public class AdminShopController extends BackOfficeController {
    @Autowired
    private ShopService shopService;
    @Autowired
    private ExchangeRateService exchangeRateService;
    @Autowired
    private GiftProductService giftProductService;
    @Autowired
    private ZbtProductFiltersService zbtProductFiltersService;

    @Autowired
    private ShopStockService shopStockService;

    @PostMapping("/product/pagination")
    public PageResponse<GiftProductResponse> pagination(@Valid @RequestBody SearchProductRequest request) {
        SearchProductCondition condition = DataConverter.to(SearchProductCondition.class, request);
        BigDecimal shopSpillPriceRate = exchangeRateService.get().getShopSpillPrice();
        return DataConverter.to(giftProductPlus -> {
            GiftProductResponse response = new GiftProductResponse();
            BeanUtils.copyProperties(giftProductPlus, response);
            BigDecimal spillPrice = giftProductPlus.getOriginAmount().multiply(shopSpillPriceRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            response.setPrice(giftProductPlus.getOriginAmount().add(spillPrice));
            response.setCsgoTypeName(zbtProductFiltersService.getNameByCsgoType(giftProductPlus.getCsgoType()));
            return response;
        }, giftProductService.pagination(condition));
    }

    @PostMapping("/pagination")
    public PageResponse<ShopResponse> pagination(@Valid @RequestBody SearchShopRequest request) {
        SearchShopCondition condition = DataConverter.to(SearchShopCondition.class, request);
        BigDecimal shopSpillPriceRate = exchangeRateService.get().getShopSpillPrice();
        return DataConverter.to(shop -> {
            ShopResponse response = new ShopResponse();
            BeanUtilsEx.copyProperties(shop, response);
            BigDecimal spillPrice = shop.getPrice().multiply(shopSpillPriceRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            response.setPrice(shop.getPrice().add(spillPrice));
            return response;
        }, shopService.pagination(condition));
    }

    @PostMapping("/findPage")
    public PageResponse<ShopResponse> findPage(@Valid @RequestBody SearchShopRequest request, HttpServletRequest req) {
        SearchShopCondition condition = DataConverter.to(SearchShopCondition.class, request, request.getSort(), request.getSortBy());
        BigDecimal exchangeRate = shopStockService.getExchangeRate();
        condition.setShopSpillPriceRate(exchangeRate);

        return DataConverter.to(shop -> {
            ShopResponse response = new ShopResponse();
            BeanUtilsEx.copyProperties(shop, response);
            return response;
        }, shopStockService.findPage(condition));
    }

    /**
     * 更新库存 - 一键添加库存
     */
    @PostMapping("/stock/batch-add")
    public BaseResponse<List<ShopResponse>> batchAddStock(@Valid @RequestBody ShopStockOneClickAddRequest request) {
        List<ShopDTO> dtoList = shopStockService.batchAddStock(request);

        List<ShopResponse> responseList = dtoList.stream().map(item -> {
            ShopResponse response = new ShopResponse();
            BeanUtilsEx.copyProperties(item, response);
            return response;
        }).collect(Collectors.toList());
        return BaseResponse.<List<ShopResponse>>builder().data(responseList).get();
    }

    @PostMapping("/batch")
    public BaseResponse<Void> addBatch(@Valid @RequestBody List<ShopRequest> requests) {
        String userName = siteContext.getCurrentUser().getName();
        List<Shop> shops = requests.stream().map(request -> {
            Shop shop = new Shop();
            BeanUtilsEx.copyProperties(request, shop);
            shop.setCb(userName);
            return shop;
        }).collect(Collectors.toList());
        shopService.addBatch(shops);
        return BaseResponse.<Void>builder().get();
    }

    @PutMapping("/batch")
    @Log(desc = "全局更新商品库存")
    public BaseResponse<Void> updateBatch() {
        shopService.updateBatch();
        return BaseResponse.<Void>builder().get();
    }

    @PutMapping("/{id}")
    public BaseResponse<Void> update(@PathVariable("id") int id, @Valid @RequestBody ShopRequest request) {
        Shop shop = shopService.get(id);
        if (shop == null) {
            throw new ApiException(400, "找不到对应商品信息");
        }
        if (!shop.getGiftProductId().equals(request.getGiftProductId())) {
            throw new BusinessException(ExceptionCode.SHOP_EXISTS);
        }
        BeanUtilsEx.copyProperties(request, shop);
        shop.setUb(siteContext.getCurrentUser().getName());
        shopService.update(shop);
        return BaseResponse.<Void>builder().get();
    }

    @DeleteMapping("/batch")
    public BaseResponse<Void> batchDelete(@Valid @RequestBody BatchDeleteShopRequest request) {
        shopService.batchDelete(request.getIds());
        return BaseResponse.<Void>builder().get();
    }
}
