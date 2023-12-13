package com.csgo.service.shop;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.shop.SearchShopOrderCondition;
import com.csgo.domain.plus.shop.ShopOrderDTO;
import com.csgo.mapper.plus.shop.ShopOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author admin
 */
@Service
public class ShopOrderService {

    @Autowired
    private ShopOrderMapper mapper;

    public Page<ShopOrderDTO> pagination(SearchShopOrderCondition condition) {
        return mapper.pagination(condition.getPage(), condition);
    }
}
