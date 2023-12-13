package com.csgo.mapper;

import com.csgo.domain.RollGift;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RollGiftMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RollGift record);

    int insertSelective(RollGift record);

    RollGift selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RollGift record);

    int updateByPrimaryKey(RollGift record);

    List<RollGift> selectByList(RollGift rollGift);

    List<RollGift> selectByGiftProductList(RollGift rollGift);
}