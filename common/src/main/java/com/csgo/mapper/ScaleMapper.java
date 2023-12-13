package com.csgo.mapper;

import com.csgo.domain.Scale;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScaleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Scale record);

    int insertSelective(Scale record);

    Scale selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Scale record);

    int updateByPrimaryKey(Scale record);

    List<Scale> selectList(Scale record);
}