package com.csgo.mapper.plus.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.domain.plus.config.ExchangeRatePlus;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author admin
 */
@Repository
public interface ExchangeRatePlusMapper extends BaseMapper<ExchangeRatePlus> {

    default List<ExchangeRatePlus> findByFlag(int flag) {
        LambdaQueryWrapper<ExchangeRatePlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExchangeRatePlus::getFlag, flag);
        return selectList(wrapper);
    }
}
