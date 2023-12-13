package com.csgo.mapper.plus.blind;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.domain.plus.blind.BlindBoxTypePlus;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author admin
 */
@Repository
public interface BlindBoxTypePlusMapper extends BaseMapper<BlindBoxTypePlus> {

    default List<BlindBoxTypePlus> findAll() {
        LambdaQueryWrapper<BlindBoxTypePlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(BlindBoxTypePlus::getSortId, BlindBoxTypePlus::getUpdateTime);
        return selectList(wrapper);
    }
}
