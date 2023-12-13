package com.csgo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.BlindBoxRoom;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlindBoxRoomMapper extends BaseMapper<BlindBoxRoom> {

    BlindBoxRoom getBoxRoomByRoomNum(@Param("roomNum") String roomNum);

    BlindBoxRoom getBoxRoomById(@Param("id") Integer id);

    Page<BlindBoxRoom> pageList(Page<BlindBoxRoom> page, @Param("keywords") String keywords, @Param("status") Integer status,
                                @Param("start_time") Long start_time, @Param("end_time") Long end_time);

    Page<BlindBoxRoom> pageRoomList(Page<BlindBoxRoom> page);

    Page<BlindBoxRoom> pageRoomHistoryList(Page<BlindBoxRoom> page, @Param("userId") Integer userId);

    int addBatch(@Param("blindBoxRoomList") List<BlindBoxRoom> blindBoxRoomList);

    void deleteById(@Param("id") Integer id);

    BlindBoxRoom getBestBattlePointsInHistory(@Param("userId") Integer userId, @Param("isToday") Integer isToday);

    void updateRoomStatus();

    int updateWaitRoomStatus(@Param("id") Integer id);

    int compulsoryUpdateWaitRoomStatus(@Param("id") Integer id);

    List<BlindBoxRoom> getWaitRoomList();
}