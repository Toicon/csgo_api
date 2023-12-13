package com.csgo.mapper;

import com.csgo.domain.GiftProductRecord;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftProductRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GiftProductRecord record);

    int insertSelective(GiftProductRecord record);

    GiftProductRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GiftProductRecord record);

    int updateByPrimaryKey(GiftProductRecord record);

    List<GiftProductRecord> getList(GiftProductRecord record);

    List<GiftProductRecord> getListOrderPrice(GiftProductRecord record);
}