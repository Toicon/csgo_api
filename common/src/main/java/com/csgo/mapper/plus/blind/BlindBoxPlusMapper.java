package com.csgo.mapper.plus.blind;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.blind.SearchBlindBoxCondition;
import com.csgo.domain.plus.blind.BlindBoxDTO;
import com.csgo.domain.plus.blind.BlindBoxPlus;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author admin
 */
@Repository
public interface BlindBoxPlusMapper extends BaseMapper<BlindBoxPlus> {

    Page<BlindBoxDTO> pagination(IPage<BlindBoxDTO> page, @Param("condition") SearchBlindBoxCondition condition);

    default BlindBoxPlus getByGiftId(int giftId) {
        LambdaQueryWrapper<BlindBoxPlus> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(BlindBoxPlus::getGiftId, giftId);
        return selectOne(wrapper);
    }

    default BlindBoxPlus get(String name) {
        LambdaQueryWrapper<BlindBoxPlus> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(BlindBoxPlus::getName, name);
        return selectOne(wrapper);
    }

    List<BlindBoxDTO> findByTypeId(Integer typeId);

    default List<BlindBoxPlus> findWithGift() {
        LambdaQueryWrapper<BlindBoxPlus> wrapper = Wrappers.lambdaQuery();
        wrapper.isNotNull(BlindBoxPlus::getGiftId);
        return selectList(wrapper);
    }
}
