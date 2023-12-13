package com.csgo.service.gift;

import com.csgo.domain.plus.gift.GiftType;
import com.csgo.mapper.plus.gift.GiftTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Admin on 2021/5/2
 */
@Service
public class GiftTypeService {
    @Autowired
    private GiftTypeMapper mapper;

    public GiftType get(int id) {
        return mapper.selectById(id);
    }

    public List<GiftType> findAll() {
        return mapper.selectAllType(false);
    }
}
