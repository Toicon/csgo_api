package com.csgo.mapper.plus.shop;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.shop.SearchShopOrderCondition;
import com.csgo.domain.plus.shop.ShopOrder;
import com.csgo.domain.plus.shop.ShopOrderDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author admin
 */
@Repository
public interface ShopOrderMapper extends BaseMapper<ShopOrder> {
    Page<ShopOrderDTO> pagination(IPage<ShopOrder> page, @Param("condition") SearchShopOrderCondition condition);
}
