package com.csgo.service;

import com.csgo.domain.plus.gift.GiftType;
import com.csgo.mapper.plus.gift.GiftTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by Admin on 2021/4/27
 */
@Service
public class GiftTypeService {

    @Autowired
    private GiftTypeMapper mapper;

    @Transactional
    public int add(GiftType giftType) {
        giftType.setCt(new Date());
        mapper.insert(giftType);
        return giftType.getId();
    }

    public List<GiftType> findAll() {
        return mapper.selectAllType(false);
    }

    @Transactional
    public int update(GiftType giftType) {
        giftType.setUt(new Date());
        return mapper.updateById(giftType);
    }

    public GiftType get(int id) {
        return mapper.selectById(id);
    }

    public List<GiftType> find() {
        return mapper.selectList(null);
    }

    @Transactional
    public int delete(int id) {
        return mapper.deleteById(id);
    }
}
