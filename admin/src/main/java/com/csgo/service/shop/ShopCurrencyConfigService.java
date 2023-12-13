package com.csgo.service.shop;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.shop.SearchShopCurrencyConfigCondition;
import com.csgo.domain.plus.shop.ShopCurrencyConfig;
import com.csgo.exception.AdminErrorException;
import com.csgo.mapper.plus.shop.ShopCurrencyConfigMapper;
import com.csgo.support.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商城货币兑换配置
 */
@Service
public class ShopCurrencyConfigService {

    @Autowired
    private ShopCurrencyConfigMapper shopCurrencyConfigMapper;


    public PageInfo<ShopCurrencyConfig> pageList(SearchShopCurrencyConfigCondition condition) {
        Page<ShopCurrencyConfig> pagination = shopCurrencyConfigMapper.pagination(condition);
        return new PageInfo<>(pagination);
    }

    @Transactional
    public void add(BigDecimal diamondAmount, BigDecimal giveRate) {
        ShopCurrencyConfig shopCurrencyConfig = new ShopCurrencyConfig();
        shopCurrencyConfig.setDiamondAmount(diamondAmount);
        if (giveRate != null) {
            shopCurrencyConfig.setGiveRate(giveRate);
        } else {
            shopCurrencyConfig.setGiveRate(BigDecimal.ZERO);
        }
        shopCurrencyConfig.setCreateDate(new Date());
        shopCurrencyConfigMapper.insert(shopCurrencyConfig);
    }

    @Transactional
    public void update(Integer id, BigDecimal diamondAmount, BigDecimal giveRate) {
        ShopCurrencyConfig shopCurrencyConfig = shopCurrencyConfigMapper.selectById(id);
        if (shopCurrencyConfig == null) {
            throw new AdminErrorException("商城货币兑换配置信息不存在,修改失败");
        }
        shopCurrencyConfig.setDiamondAmount(diamondAmount);
        if (giveRate != null) {
            shopCurrencyConfig.setGiveRate(giveRate);
        } else {
            shopCurrencyConfig.setGiveRate(BigDecimal.ZERO);
        }
        shopCurrencyConfig.setUpdateDate(new Date());
        shopCurrencyConfigMapper.updateById(shopCurrencyConfig);
    }

    @Transactional
    public void delete(Integer id) {
        ShopCurrencyConfig shopCurrencyConfig = shopCurrencyConfigMapper.selectById(id);
        if (shopCurrencyConfig == null) {
            throw new AdminErrorException("商城货币兑换配置信息不存在,删除失败");
        }
        shopCurrencyConfigMapper.deleteById(id);
    }
}
