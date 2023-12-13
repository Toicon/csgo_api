package com.csgo.mapper.plus.shop;

import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.shop.SearchShopCondition;
import com.csgo.domain.plus.shop.Shop;
import com.csgo.domain.plus.shop.ShopDTO;
import com.csgo.domain.plus.shop.ShopStatus;

/**
 * @author admin
 */
@Repository
public interface ShopMapper extends BaseMapper<Shop> {
    Page<ShopDTO> pagination(IPage<ShopDTO> page, @Param("condition") SearchShopCondition condition);

    Page<ShopDTO> findPage(IPage<ShopDTO> page, @Param("condition") SearchShopCondition condition);

    List<ShopDTO> listByIds(@Param("shopSpillPriceRate") BigDecimal shopSpillPriceRate, @Param("shopIds") List<Long> shopIds);

    default List<Shop> find(ShopStatus status) {
        LambdaQueryWrapper<Shop> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Shop::getStatus, status);
        return selectList(wrapper);
    }

    default Shop get(int giftProductId) {
        LambdaQueryWrapper<Shop> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Shop::getStatus, ShopStatus.NORMAL);
        wrapper.eq(Shop::getGiftProductId, giftProductId);
        return selectOne(wrapper);
    }

    List<Integer> findAllProductId();
}
