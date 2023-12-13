package com.csgo.mapper;

import com.csgo.domain.GiftPrice;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftPriceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GiftPrice record);

    int insertSelective(GiftPrice record);

    GiftPrice selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GiftPrice record);

    int updateByPrimaryKey(GiftPrice record);

    GiftPrice getOne(GiftPrice giftProduct);

    List<GiftPrice> getList(GiftPrice record);
}