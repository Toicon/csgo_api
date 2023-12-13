package com.csgo.service.blind;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.BlindBoxRoom;
import com.csgo.domain.plus.blind.BlindBoxTurnPlus;
import com.csgo.mapper.BlindBoxRoomMapper;
import com.csgo.mapper.plus.blind.BlindBoxTurnPlusMapper;
import com.csgo.support.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author admin
 */
@Service
public class BlindBoxRoomService {

    @Autowired
    private BlindBoxRoomMapper blindBoxRoomMapper;
    @Autowired
    private BlindBoxTurnPlusMapper blindBoxTurnMapper;

    public PageInfo<BlindBoxRoom> pageList(Integer pageNum, Integer pageSize, String keywords, Integer status,
                                           Long startTime, Long endTime) {
        Page<BlindBoxRoom> page = new Page<>(pageNum, pageSize);
        Page<BlindBoxRoom> blindBoxList = blindBoxRoomMapper.pageList(page, keywords, status, startTime, endTime);
        return new PageInfo<>(blindBoxList);
    }

    public List<BlindBoxTurnPlus> find(String roomNum, Integer turn) {
        return blindBoxTurnMapper.find(roomNum, turn);
    }

    public BlindBoxRoom getBlindBoxRoomByRoomNum(String roomNum) {
        return blindBoxRoomMapper.getBoxRoomByRoomNum(roomNum);
    }

    public int compulsoryUpdateWaitRoomStatus(Integer id) {
        return blindBoxRoomMapper.compulsoryUpdateWaitRoomStatus(id);
    }

    public void deleteBath(List<Integer> ids) {
        for (Integer id : ids) {
            blindBoxRoomMapper.deleteById(id);
        }
    }

}
