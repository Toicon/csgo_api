package com.csgo.mapper;

import com.csgo.domain.GiftGrade;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftGradeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GiftGrade record);

    int insertSelective(GiftGrade record);

    GiftGrade selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GiftGrade record);

    int updateByPrimaryKey(GiftGrade record);

    GiftGrade selectOne(GiftGrade record);

    List<GiftGrade> selectList(GiftGrade record);

    List<GiftGrade> selectListLt(GiftGrade record);
}