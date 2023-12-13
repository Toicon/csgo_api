package com.csgo.service.gift;

import com.csgo.domain.GiftGrade;
import com.csgo.domain.plus.gift.GiftGradePlus;
import com.csgo.mapper.GiftGradeMapper;
import com.csgo.mapper.plus.gift.GiftGradePlusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GiftGradeService {

    @Autowired
    private GiftGradeMapper mapper;
    @Autowired
    private GiftGradePlusMapper giftGradePlusMapper;

    public int add(GiftGrade grade) {
        return mapper.insert(grade);
    }

    public int update(GiftGrade giftGrade, int id) {
        giftGrade.setId(id);
        return mapper.updateByPrimaryKey(giftGrade);
    }

    public GiftGrade getByGrade(String grade) {
        GiftGrade record = new GiftGrade();
        record.setGrade(grade);
        return mapper.selectOne(record);
    }

    public List<GiftGradePlus> findAll() {
        return giftGradePlusMapper.findAll();
    }

    public GiftGrade queryById(int id) {
        return mapper.selectByPrimaryKey(id);
    }

    public int delete(int id) {
        return mapper.deleteByPrimaryKey(id);
    }

    public List<GiftGrade> queryAll(GiftGrade giftGrade) {
        return mapper.selectList(giftGrade);
    }
}
