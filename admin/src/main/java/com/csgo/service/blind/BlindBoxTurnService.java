package com.csgo.service.blind;

import com.csgo.domain.plus.user.UserPlus;
import com.csgo.mapper.plus.blind.BlindBoxTurnPlusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author admin
 */
@Service
public class BlindBoxTurnService {

    @Autowired
    private BlindBoxTurnPlusMapper mapper;

    public List<UserPlus> findByRoomNum(String roomNum) {
        return mapper.findByRoomNum(roomNum);
    }
}
