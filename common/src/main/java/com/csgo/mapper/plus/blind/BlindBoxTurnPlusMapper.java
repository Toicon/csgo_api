package com.csgo.mapper.plus.blind;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.domain.plus.blind.BlindBoxTurnPlus;
import com.csgo.domain.plus.blind.BlindBoxUserStarDTO;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.domain.user.UserRoomImgDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author admin
 */
@Repository
public interface BlindBoxTurnPlusMapper extends BaseMapper<BlindBoxTurnPlus> {

    List<UserRoomImgDTO> findUserImgByRoomList(@Param("roomList") List<String> roomList);

    List<UserPlus> findByRoomNum(@Param("roomNum") String roomNum);

    List<BlindBoxUserStarDTO> findRankingOfPoints(Integer limit);

    BlindBoxUserStarDTO getYesterdayStar();

    default void deleteByUserIdAndRoomNum(String roomNum, int userId) {
        LambdaQueryWrapper<BlindBoxTurnPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlindBoxTurnPlus::getRoomNum, roomNum);
        wrapper.eq(BlindBoxTurnPlus::getUserId, userId);
        delete(wrapper);
    }

    default List<BlindBoxTurnPlus> find(String roomNum, Integer turn) {
        LambdaQueryWrapper<BlindBoxTurnPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlindBoxTurnPlus::getRoomNum, roomNum);
        if (turn != null) {
            wrapper.eq(BlindBoxTurnPlus::getTurn, turn);
        }
        return selectList(wrapper);
    }

    default List<BlindBoxTurnPlus> findByUserId(String roomNum, Integer userId) {
        LambdaQueryWrapper<BlindBoxTurnPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlindBoxTurnPlus::getRoomNum, roomNum);
        if (userId != null) {
            wrapper.eq(BlindBoxTurnPlus::getUserId, userId);
        }
        return selectList(wrapper);
    }

    default BlindBoxTurnPlus get(String roomNum, int turn, int userId) {
        LambdaQueryWrapper<BlindBoxTurnPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlindBoxTurnPlus::getRoomNum, roomNum);
        wrapper.eq(BlindBoxTurnPlus::getTurn, turn);
        wrapper.eq(BlindBoxTurnPlus::getUserId, userId);
        return selectOne(wrapper);
    }

    default BlindBoxTurnPlus getBySeat(String roomNum, int seat) {
        LambdaQueryWrapper<BlindBoxTurnPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlindBoxTurnPlus::getRoomNum, roomNum);
        wrapper.eq(BlindBoxTurnPlus::getSeat, seat);
        return selectOne(wrapper);
    }
}
