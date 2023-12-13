package com.csgo.modular.bomb.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.modular.bomb.domain.NovBombGamePlayDO;
import com.csgo.modular.bomb.enums.NovBombGameStatus;
import com.csgo.modular.bomb.enums.NovBombPlayType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author admin
 */
@Repository
public interface NovBombGamePlayMapper extends BaseMapper<NovBombGamePlayDO> {

    default List<NovBombGamePlayDO> getRewardHistory(Integer num, Integer gameId) {
        LambdaQueryWrapper<NovBombGamePlayDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NovBombGamePlayDO::getStatus, NovBombGameStatus.FINISHED.getCode());
        wrapper.eq(NovBombGamePlayDO::getGameId, gameId);
        wrapper.orderByDesc(NovBombGamePlayDO::getId);
        wrapper.last(String.format("limit %s", num));
        return selectList(wrapper);
    }

    default NovBombGamePlayDO getLastByGameId(Integer gameId) {
        LambdaQueryWrapper<NovBombGamePlayDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NovBombGamePlayDO::getGameId, gameId);
        wrapper.orderByDesc(NovBombGamePlayDO::getId);
        wrapper.last("limit 1");
        return selectOne(wrapper);
    }

    default Integer countByGameIdAndPlayType(Integer gameId, NovBombPlayType playType) {
        LambdaQueryWrapper<NovBombGamePlayDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NovBombGamePlayDO::getGameId, gameId);
        wrapper.eq(NovBombGamePlayDO::getPlayType, playType.getCode());
        return selectCount(wrapper);
    }

    default NovBombGamePlayDO getLastUnFinishPlay(Integer userId) {
        LambdaQueryWrapper<NovBombGamePlayDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NovBombGamePlayDO::getUserId, userId);
        wrapper.eq(NovBombGamePlayDO::getStatus, NovBombGameStatus.ING.getCode());

        wrapper.orderByDesc(NovBombGamePlayDO::getId);
        wrapper.last("limit 1");
        return selectOne(wrapper);
    }

    default NovBombGamePlayDO getLastByUserId(Integer userId, Integer gameId) {
        LambdaQueryWrapper<NovBombGamePlayDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NovBombGamePlayDO::getUserId, userId);
        wrapper.eq(NovBombGamePlayDO::getGameId, gameId);

        wrapper.orderByDesc(NovBombGamePlayDO::getId);
        wrapper.last("limit 1");
        return selectOne(wrapper);
    }

    default List<NovBombGamePlayDO> listByGameId(Integer gameId) {
        LambdaQueryWrapper<NovBombGamePlayDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NovBombGamePlayDO::getGameId, gameId);
        wrapper.orderByAsc(NovBombGamePlayDO::getId);
        return selectList(wrapper);
    }

}
