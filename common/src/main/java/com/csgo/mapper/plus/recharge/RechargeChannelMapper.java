package com.csgo.mapper.plus.recharge;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.domain.plus.recharge.RechargeChannel;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author admin
 */
@Repository
public interface RechargeChannelMapper extends BaseMapper<RechargeChannel> {

    default List<RechargeChannel> find(boolean isApp) {
        LambdaQueryWrapper<RechargeChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RechargeChannel::isHidden, true);
        wrapper.eq(RechargeChannel::isApp, isApp);
        wrapper.orderByAsc(RechargeChannel::getSortId);
        return selectList(wrapper);
    }

}
