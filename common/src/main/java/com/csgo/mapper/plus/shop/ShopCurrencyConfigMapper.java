package com.csgo.mapper.plus.shop;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.shop.SearchShopCurrencyConfigCondition;
import com.csgo.domain.plus.shop.ShopCurrencyConfig;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商城货币兑换配置
 *
 * @author admin
 */
@Repository
public interface ShopCurrencyConfigMapper extends BaseMapper<ShopCurrencyConfig> {

    default Page<ShopCurrencyConfig> pagination(SearchShopCurrencyConfigCondition condition) {
        LambdaQueryWrapper<ShopCurrencyConfig> wrapper = Wrappers.lambdaQuery();
        wrapper.orderByAsc(ShopCurrencyConfig::getDiamondAmount);
        return selectPage(condition.getPage(), wrapper);
    }

    default List<ShopCurrencyConfig> findAll() {
        LambdaQueryWrapper<ShopCurrencyConfig> wrapper = Wrappers.lambdaQuery();
        wrapper.orderByAsc(ShopCurrencyConfig::getDiamondAmount);
        return selectList(wrapper);
    }

}
