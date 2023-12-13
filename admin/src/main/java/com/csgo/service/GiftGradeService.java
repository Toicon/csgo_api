package com.csgo.service;

import com.csgo.domain.GiftGrade;
import com.csgo.mapper.GiftGradeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GiftGradeService {

    @Autowired
    private GiftGradeMapper mapper;

    public int add(GiftGrade grade) {
        return mapper.insert(grade);
    }


    public int update(GiftGrade giftGrade, int id) {
        giftGrade.setId(id);
        return mapper.updateByPrimaryKey(giftGrade);
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

    public List<GiftGrade> queryAllLt(GiftGrade giftGrade) {
        return mapper.selectListLt(giftGrade);
    }
}
