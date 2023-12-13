package com.csgo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.GiftProduct;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftProductMapper extends BaseMapper<GiftProduct> {
    int deleteByPrimaryKey(int id);

    int insert(GiftProduct record);

    int insertSelective(GiftProduct record);

    GiftProduct selectByPrimaryKey(int id);

    int updateByPrimaryKey(GiftProduct record);

    List<GiftProduct> getList(GiftProduct record);

    List<GiftProduct> getListLt(GiftProduct record);

    Page<GiftProduct> pageList(Page<GiftProduct> page, @Param("keywords") String keywords, @Param("csgoType") String csgoType, @Param("typeId") Integer typeId);

    Page<GiftProduct> randomPageList(Page<GiftProduct> page, @Param("keywords") String keywords, @Param("csgoType") String csgoType, @Param("luckyId") Integer luckyId);

    Page<GiftProduct> pageProductList(Page<GiftProduct> page, @Param("keywords") String keywords, @Param("isLuckyProduct") Integer isLuckyProduct);

    List<GiftProduct> getListByGiftId(@Param("giftId") Integer giftId);
}