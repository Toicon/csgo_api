package com.csgo.mapper;

import com.csgo.domain.Gift;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftMapper {
    int deleteByPrimaryKey(int id);

    int insert(Gift record);

    int insertSelective(Gift record);

    Gift selectByPrimaryKey(int id);

    int updateByPrimaryKeySelective(Gift record);

    int updateByPrimaryKey(Gift record);

    List<Gift> getList(Gift record);

    List<Gift> getListByPage(Gift record);

    List<Gift> getTypeList(int type);

    int deleteByPrimaryKeyTypeId(Integer id);

    List<Gift> queryAllGift();

    void emptyCount();

    void emptyCount2();
}