package com.csgo.mapper;

import com.csgo.domain.PaySet;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaySetMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PaySet record);

    int insertSelective(PaySet record);

    PaySet selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PaySet record);

    int updateByPrimaryKey(PaySet record);

    PaySet selectOne(PaySet record);

    List<PaySet> selectList(PaySet record);

    List<PaySet> selectListLt(PaySet record);
}