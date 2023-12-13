package com.csgo.mapper.plus.recharge;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.domain.plus.recharge.RechargeChannelPriceItem;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author admin
 */
@Repository
public interface RechargeChannelPriceItemMapper extends BaseMapper<RechargeChannelPriceItem> {

    default List<RechargeChannelPriceItem> findByChannelIds(List<Integer> channelIds) {
        LambdaQueryWrapper<RechargeChannelPriceItem> wrapper = new LambdaQueryWrapper<>();
        if (!CollectionUtils.isEmpty(channelIds)) {
            wrapper.in(RechargeChannelPriceItem::getChannelId, channelIds);
        }
        return selectList(wrapper);
    }

    default RechargeChannelPriceItem findMinItem(int channelId) {
        LambdaQueryWrapper<RechargeChannelPriceItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RechargeChannelPriceItem::getChannelId, channelId);
        wrapper.orderByAsc(RechargeChannelPriceItem::getPrice);
        wrapper.last(" limit 1 ");
        return selectOne(wrapper);
    }
}
