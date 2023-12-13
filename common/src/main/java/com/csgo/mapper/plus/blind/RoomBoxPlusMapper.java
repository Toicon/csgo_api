package com.csgo.mapper.plus.blind;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.domain.plus.blind.RoomBoxDTO;
import com.csgo.domain.plus.blind.RoomBoxPlus;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author admin
 */
@Repository
public interface RoomBoxPlusMapper extends BaseMapper<RoomBoxPlus> {

    List<RoomBoxDTO> findDTOByRoomNum(String roomNum);

    default List<RoomBoxPlus> findByRoomNum(String roomNum) {
         LambdaQueryWrapper<RoomBoxPlus> wrapper = new LambdaQueryWrapper<>();
         wrapper.eq(RoomBoxPlus::getRoomNum, roomNum);
         return selectList(wrapper);
    }

    default List<RoomBoxPlus> findByRoomNumList(List<String> roomList) {
        if (CollectionUtils.isEmpty(roomList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<RoomBoxPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(RoomBoxPlus::getRoomNum, roomList);
        return selectList(wrapper);
    }
}
