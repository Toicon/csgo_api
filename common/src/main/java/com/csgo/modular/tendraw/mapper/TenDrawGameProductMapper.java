package com.csgo.modular.tendraw.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.modular.tendraw.domain.TenDrawProductDO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author admin
 */
@Repository
public interface TenDrawGameProductMapper extends BaseMapper<TenDrawProductDO> {

    default List<TenDrawProductDO> listByGameId(Integer gameId) {
        LambdaQueryWrapper<TenDrawProductDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TenDrawProductDO::getGameId, gameId);
        return selectList(wrapper);
    }

}
