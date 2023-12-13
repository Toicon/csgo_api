package com.csgo.mapper.plus.envelop;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.csgo.domain.enums.RedEnvelopStatus;
import com.csgo.domain.plus.envelop.RedEnvelopItem;
import com.csgo.domain.plus.envelop.RedEnvelopItemDTO;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RedEnvelopItemMapper extends BaseMapper<RedEnvelopItem> {

    default List<RedEnvelopItem> find(List<Integer> redEnvelopIds) {
        LambdaQueryWrapper<RedEnvelopItem> wrapper = Wrappers.lambdaQuery();
        wrapper.in(RedEnvelopItem::getEnvelopId, redEnvelopIds);
        wrapper.eq(RedEnvelopItem::getStatus, RedEnvelopStatus.NORMAL);
        wrapper.orderByDesc(RedEnvelopItem::getEffectiveEndTime);
        return selectList(wrapper);
    }

    List<RedEnvelopItemDTO> findAll();

    default RedEnvelopItem get(String token) {
        LambdaQueryWrapper<RedEnvelopItem> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RedEnvelopItem::getToken, token);
        wrapper.eq(RedEnvelopItem::getStatus, RedEnvelopStatus.NORMAL);
        return selectOne(wrapper);
    }

    default RedEnvelopItem get(int envelopId, Date date) {
        LambdaQueryWrapper<RedEnvelopItem> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RedEnvelopItem::getEnvelopId, envelopId);
        wrapper.eq(RedEnvelopItem::getStatus, RedEnvelopStatus.NORMAL);
        if (date != null) {
            wrapper.ge(RedEnvelopItem::getEffectiveStartTime, date);
            wrapper.le(RedEnvelopItem::getEffectiveEndTime, date);
        }
        return selectOne(wrapper);
    }

    default List<RedEnvelopItem> findByEnvelopId(Integer envelopId) {
        LambdaQueryWrapper<RedEnvelopItem> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RedEnvelopItem::getEnvelopId, envelopId);
        wrapper.orderByDesc(RedEnvelopItem::getCreateDate);
        return selectList(wrapper);
    }

    default RedEnvelopItem getByEnvelopId(int redEnvelopId) {
        LambdaQueryWrapper<RedEnvelopItem> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RedEnvelopItem::getEnvelopId, redEnvelopId);
        wrapper.eq(RedEnvelopItem::getStatus, RedEnvelopStatus.NORMAL);
        return selectOne(wrapper);
    }
}