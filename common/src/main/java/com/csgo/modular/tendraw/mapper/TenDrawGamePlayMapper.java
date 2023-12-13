package com.csgo.modular.tendraw.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.modular.tendraw.domain.TenDrawGamePlayDO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author admin
 */
@Repository
public interface TenDrawGamePlayMapper extends BaseMapper<TenDrawGamePlayDO> {

    default List<TenDrawGamePlayDO> listByGameId(Integer gameId) {
        LambdaQueryWrapper<TenDrawGamePlayDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TenDrawGamePlayDO::getGameId, gameId);
        wrapper.orderByAsc(TenDrawGamePlayDO::getId);
        return selectList(wrapper);
    }

    default TenDrawGamePlayDO getLastByGameId(Integer gameId) {
        LambdaQueryWrapper<TenDrawGamePlayDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TenDrawGamePlayDO::getGameId, gameId);
        wrapper.orderByDesc(TenDrawGamePlayDO::getId);
        wrapper.last("limit 1");
        return selectOne(wrapper);
    }

    default TenDrawGamePlayDO getByGameIdAndPlayIndex(Integer gameId, Integer playIndex) {
        LambdaQueryWrapper<TenDrawGamePlayDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TenDrawGamePlayDO::getGameId, gameId);
        wrapper.eq(TenDrawGamePlayDO::getPlayIndex, playIndex);
        wrapper.last("limit 1");
        return selectOne(wrapper);
    }

    default Integer countByGameId(Integer gameId) {
        LambdaQueryWrapper<TenDrawGamePlayDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TenDrawGamePlayDO::getGameId, gameId);
        return selectCount(wrapper);
    }

}
