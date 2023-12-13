package com.csgo.mapper.plus.gift;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.csgo.domain.plus.gift.GiftType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftTypeMapper extends BaseMapper<GiftType> {

    default List<GiftType> selectAllType(Boolean hidden) {
        LambdaQueryWrapper<GiftType> wrapper = Wrappers.lambdaQuery();
        if (null != hidden) {
            wrapper.eq(GiftType::isHidden, hidden);
        }
        wrapper.orderByDesc(GiftType::getSort, GiftType::getCt);
        return selectList(wrapper);
    }
}