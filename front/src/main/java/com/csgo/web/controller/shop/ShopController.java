package com.csgo.web.controller.shop;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.validation.Valid;

import com.csgo.condition.shop.SearchUserCurrencyExchangeCondition;
import com.csgo.constants.CommonBizCode;
import com.csgo.domain.plus.shop.ShopCurrencyConfig;
import com.csgo.domain.plus.shop.UserCurrencyExchangeDTO;
import com.csgo.framework.exception.BizClientException;
import com.csgo.web.request.shop.SearchUserCurrencyExchangeRequest;
import com.csgo.web.request.shop.UserCurrencyExchangeRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.shop.SearchShopProductCondition;
import com.csgo.domain.plus.shop.ShopGiftProductDTO;
import com.csgo.service.ExchangeRateService;
import com.csgo.service.shop.ShopService;
import com.csgo.service.user.BalanceSupportService;
import com.csgo.support.DataConverter;
import com.csgo.support.GlobalConstants;
import com.csgo.support.StandardExceptionCode;
import com.csgo.util.SignUtil;
import com.csgo.web.intecepter.LoginRequired;
import com.csgo.web.request.shop.BuyShopGiftProductRequest;
import com.csgo.web.request.shop.SearchShopProductRequest;
import com.csgo.web.response.gift.ShopGiftProductResponse;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.interceptor.session.RequireSession;
import com.echo.framework.platform.web.Api;
import com.echo.framework.platform.web.response.BaseResponse;
import com.echo.framework.platform.web.response.PageResponse;

/**
 * @author admin
 */
@Api
@RequireSession
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;
    @Autowired
    private SiteContext siteContext;
    @Autowired
    private ExchangeRateService exchangeRateService;
    @Autowired
    private BalanceSupportService balanceSupportService;

    @PostMapping("/pagination")
    public PageResponse<ShopGiftProductResponse> pagination(@Valid @RequestBody SearchShopProductRequest request) {
        SearchShopProductCondition condition = DataConverter.to(SearchShopProductCondition.class, request);
        condition.setSortType(request.getSortType().name());
        Page<ShopGiftProductDTO> pagination = shopService.pagination(condition);
        return DataConverter.to(giftProductPlus -> {
            ShopGiftProductResponse response = new ShopGiftProductResponse();
            BeanUtils.copyProperties(giftProductPlus, response);
            response.setPrice(giftProductPlus.getPrice());
            return response;
        }, pagination);
    }

    @LoginRequired
    @PostMapping("/buy")
    public BaseResponse<Integer> buy(@Valid @RequestBody BuyShopGiftProductRequest request) {
        int userId = siteContext.getCurrentUser().getId();

        Map<String, Object> paramMap = new TreeMap<>();
        paramMap.put("giftProductId", request.getGiftProductId());
        paramMap.put("userId", userId);
        paramMap.put("key", GlobalConstants.SIGN_KEY);
        String sign = SignUtil.sign(paramMap);
        if (!sign.equals(request.getToken())) {
            throw BizClientException.of(CommonBizCode.COMMON_PARAM_ILLEGAL);
        }
        int stock = balanceSupportService.shopBuy(userId, request.getGiftProductId());
        return BaseResponse.<Integer>builder().data(stock).get();
    }

    /**
     * 获取获取兑换货币列表
     *
     * @return
     */
    @ApiOperation("获取兑换货币列表")
    @GetMapping("/findShopCurrencyConfigList")
    public BaseResponse<List<ShopCurrencyConfig>> findShopCurrencyConfigList() {
        return BaseResponse.<List<ShopCurrencyConfig>>builder().data(shopService.findShopCurrencyConfigList()).get();
    }

    /**
     * 商城货币兑换记录
     *
     * @return
     */
    @ApiOperation("商城货币兑换记录")
    @LoginRequired
    @PostMapping("/findUserCurrencyExchangeList")
    public PageResponse<UserCurrencyExchangeDTO> findUserCurrencyExchangeList(@Valid @RequestBody SearchUserCurrencyExchangeRequest request) {
        int userId = siteContext.getCurrentUser().getId();
        SearchUserCurrencyExchangeCondition condition = DataConverter.to(SearchUserCurrencyExchangeCondition.class, request);
        condition.setUserId(userId);
        return DataConverter.to(userCurrencyExchange -> {
            UserCurrencyExchangeDTO response = new UserCurrencyExchangeDTO();
            response.setDiamondAmount(userCurrencyExchange.getDiamondAmount());
            response.setGiveAmount(userCurrencyExchange.getGiveAmount());
            response.setCreateDate(userCurrencyExchange.getCreateDate());
            return response;
        }, shopService.findUserCurrencyExchangeList(condition));
    }

    @ApiOperation("货币兑换")
    @LoginRequired
    @PostMapping("/exchange")
    public BaseResponse<Void> exchange(@Valid @RequestBody UserCurrencyExchangeRequest request) {
        int userId = siteContext.getCurrentUser().getId();
        shopService.exchange(userId, request.getConfigId());
        return BaseResponse.<Void>builder().get();
    }

    @GetMapping("/init/stock")
    public BaseResponse<Void> initStock() {
        shopService.initStock();
        return BaseResponse.<Void>builder().get();
    }
}
