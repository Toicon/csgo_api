package com.csgo.service.fish;

import com.csgo.domain.Gift;
import com.csgo.mapper.GiftMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GiftService {

    @Autowired
    private GiftMapper giftMapper;

    public Gift queryGiftId(int id) {
        return giftMapper.selectByPrimaryKey(id);
    }

}
