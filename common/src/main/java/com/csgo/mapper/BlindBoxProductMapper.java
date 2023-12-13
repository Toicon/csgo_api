package com.csgo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.domain.BlindBoxProduct;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BlindBoxProductMapper extends BaseMapper<BlindBoxProduct> {

    BlindBoxProduct selectByGiftProductId(@Param("giftProductId") Integer giftProductId, @Param("blindBoxId") Integer blindBoxId);

    void deleteByBoxId(@Param("blindBoxId") Integer blindBoxId);

    void updateProbabilityBath(@Param("idList") List<Integer> idList, @Param("probability") Double probability);

    void updatePriceByGiftProductId(@Param("giftProductId") Integer giftProductId, @Param("price") BigDecimal price);
}
