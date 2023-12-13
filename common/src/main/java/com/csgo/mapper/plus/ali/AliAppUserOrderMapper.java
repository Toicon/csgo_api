package com.csgo.mapper.plus.ali;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.ali.SearchAliAppUserOrderCondition;
import com.csgo.domain.plus.ali.AliAppUserOrder;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 支付宝小程序用户订单
 */
@Repository
public interface AliAppUserOrderMapper extends BaseMapper<AliAppUserOrder> {

    default Page<AliAppUserOrder> pagination(SearchAliAppUserOrderCondition condition) {
        LambdaQueryWrapper<AliAppUserOrder> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(AliAppUserOrder::getUserId, condition.getUserId());
        if (condition.getOrderStatus() != null) {
            wrapper.eq(AliAppUserOrder::getOrderStatus, condition.getOrderStatus());
        }
        wrapper.orderByDesc(AliAppUserOrder::getCreateDate);
        return selectPage(condition.getPage(), wrapper);
    }


    default List<AliAppUserOrder> getSumOrderPrice(int userId) {
        LambdaQueryWrapper<AliAppUserOrder> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(AliAppUserOrder::getUserId, userId);
        return selectList(wrapper);
    }
}
