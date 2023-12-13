package com.csgo.mapper.plus.blind;

import java.util.Collection;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.blind.SearchBlindBoxProductCondition;
import com.csgo.domain.plus.blind.BlindBoxProductDTO;
import com.csgo.domain.plus.blind.BlindBoxProductPlus;

@Repository
public interface BlindBoxProductPlusMapper extends BaseMapper<BlindBoxProductPlus> {

    String findBoxNames(@Param("giftProductId") Integer giftProductId);

    List<BlindBoxProductDTO> findByBoxId(@Param("blindBoxId") Integer blindBoxId);

    List<BlindBoxProductDTO> findByBoxIds(@Param("blindBoxIds") Collection<Integer> blindBoxIds);

    default void deleteByBoxId(Integer boxId) {
        LambdaQueryWrapper<BlindBoxProductPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlindBoxProductPlus::getBlindBoxId, boxId);
        delete(wrapper);
    }

    Page<BlindBoxProductDTO> pagination(IPage<BlindBoxProductDTO> page, @Param("condition") SearchBlindBoxProductCondition condition);

    List<Integer> findAllProductId();
}
