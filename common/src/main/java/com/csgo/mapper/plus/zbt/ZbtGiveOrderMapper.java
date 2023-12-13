package com.csgo.mapper.plus.zbt;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.zbt.SearchZbtGiveOrderCondition;
import com.csgo.domain.plus.zbt.ZbtGiveOrder;
import com.csgo.domain.plus.zbt.ZbtGiveOrderStatus;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author admin
 */
@Repository
public interface ZbtGiveOrderMapper extends BaseMapper<ZbtGiveOrder> {


    default Page<ZbtGiveOrder> pagination(SearchZbtGiveOrderCondition condition) {
        LambdaQueryWrapper<ZbtGiveOrder> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.hasText(condition.getName())) {
            wrapper.like(ZbtGiveOrder::getName, condition.getName());
        }
        if (StringUtils.hasText(condition.getStatus())) {
            wrapper.eq(ZbtGiveOrder::getStatus, condition.getStatus());
        }
        if (StringUtils.hasText(condition.getCb())) {
            wrapper.eq(ZbtGiveOrder::getCb, condition.getCb());
        }
        wrapper.orderByDesc(ZbtGiveOrder::getCt);
        return selectPage(condition.getPage(), wrapper);
    }

    default List<ZbtGiveOrder> findByStatus(List<ZbtGiveOrderStatus> statuses) {
        LambdaQueryWrapper<ZbtGiveOrder> wrapper = Wrappers.lambdaQuery();
        wrapper.in(ZbtGiveOrder::getStatus, statuses);
        return selectList(wrapper);
    }
}
