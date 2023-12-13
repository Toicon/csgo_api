package com.csgo.mapper;

import com.csgo.domain.RandomProductDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RandomProductMapper {

    RandomProductDO selectByGiftProductId(@Param("id") Integer id, @Param("luckyId") Integer luckyId);

    List<RandomProductDO> getRandomProductList(@Param("luckyId") Integer luckyId);

    List<RandomProductDO> getRandomByLuckId(@Param("luckyId") Integer luckyId);

    void updatePriceByGiftProductId(@Param("giftProductId") Integer giftProductId, @Param("price") BigDecimal price);
}
