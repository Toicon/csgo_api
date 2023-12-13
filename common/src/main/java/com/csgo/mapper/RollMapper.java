package com.csgo.mapper;

import com.csgo.domain.Roll;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RollMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Roll record);

    int insertSelective(Roll record);

    Roll selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Roll record);

    int updateByPrimaryKey(Roll record);

    List<Roll> selectByList(Roll roll);

    List<Roll> selectByRollList(Roll roll);

    List<Roll> selectByRollUserIdList(Roll roll);
}