package com.csgo.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.domain.LuckyProductDO;

@Repository
public interface LuckyProductMapper extends BaseMapper<LuckyProductDO> {
    LuckyProductDO selectByGiftProductId(@Param("id") Integer id);

    void updatePriceByGiftProductId(@Param("giftProductId") Integer giftProductId, @Param("price") BigDecimal price);

    List<Integer> findAllProductId();
}
