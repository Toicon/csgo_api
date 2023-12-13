package com.csgo.mapper.plus.blind;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.blind.SearchBlindBoxRoomCondition;
import com.csgo.domain.plus.blind.BlindBoxRoomPlus;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author admin
 */
@Repository
public interface BlindBoxRoomPlusMapper extends BaseMapper<BlindBoxRoomPlus> {

    default Page<BlindBoxRoomPlus> pagination(Page<BlindBoxRoomPlus> page) {
        LambdaQueryWrapper<BlindBoxRoomPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(BlindBoxRoomPlus::getStatus, 3);
        wrapper.orderByDesc(BlindBoxRoomPlus::getAddTime);
        return selectPage(page, wrapper);
    }

    Page<BlindBoxRoomPlus> paginationHistory(IPage<BlindBoxRoomPlus> page, @Param("condition") SearchBlindBoxRoomCondition condition);

    default BlindBoxRoomPlus get(String roomNum) {
        LambdaQueryWrapper<BlindBoxRoomPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlindBoxRoomPlus::getRoomNum, roomNum);
        return selectOne(wrapper);
    }

    BlindBoxRoomPlus getBestBattlePointsInHistory(int userId, Integer isToday);
}
