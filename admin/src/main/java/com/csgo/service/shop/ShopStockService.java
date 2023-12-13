package com.csgo.service.shop;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.shop.SearchShopCondition;
import com.csgo.domain.plus.shop.Shop;
import com.csgo.domain.plus.shop.ShopDTO;
import com.csgo.mapper.plus.shop.ShopMapper;
import com.csgo.service.ExchangeRateService;
import com.csgo.web.request.shop.ShopStockOneClickAddRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author admin
 */
@Service
public class ShopStockService {

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private ExchangeRateService exchangeRateService;

    public Page<ShopDTO> findPage(SearchShopCondition condition) {
        return shopMapper.findPage(condition.getPage(), condition);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<ShopDTO> batchAddStock(ShopStockOneClickAddRequest request) {
        List<Shop> shops = shopMapper.selectBatchIds(request.getShopIds());
        for (Shop shop : shops) {
            shop.setStock(shop.getStock() + request.getStock());
            shopMapper.updateById(shop);
        }

        BigDecimal exchangeRate = getExchangeRate();
        return shopMapper.listByIds(exchangeRate, request.getShopIds());
    }

    public BigDecimal getExchangeRate() {
        BigDecimal rate = new BigDecimal("1");
        BigDecimal shopSpillPriceRate = exchangeRateService.get().getShopSpillPrice();
        if (shopSpillPriceRate != null) {
            return rate.add(shopSpillPriceRate.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
        }
        return rate;
    }

}
