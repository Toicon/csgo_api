package com.csgo.mapper;

import com.csgo.domain.GiftCount;
import org.springframework.stereotype.Repository;

@Repository
public interface GiftCountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GiftCount record);

    int insertSelective(GiftCount record);

    GiftCount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GiftCount record);

    int updateByPrimaryKey(GiftCount record);

    GiftCount getOnt(GiftCount record);
}