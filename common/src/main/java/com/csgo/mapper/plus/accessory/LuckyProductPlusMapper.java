package com.csgo.mapper.plus.accessory;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.accessory.SearchLuckyProductCondition;
import com.csgo.domain.plus.accessory.LuckyProduct;
import com.csgo.domain.plus.accessory.LuckyProductDTO;

/**
 * @author admin
 */
@Repository
public interface LuckyProductPlusMapper extends BaseMapper<LuckyProduct> {
    LuckyProductDTO get(int id);

    Page<LuckyProductDTO> pagination(IPage<LuckyProductDTO> page, @Param("condition") SearchLuckyProductCondition condition);

    default List<LuckyProduct> findByGiftProductId(Integer originalId) {
        LambdaQueryWrapper<LuckyProduct> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(LuckyProduct::getGiftProductId, originalId);
        return selectList(wrapper);
    }

    List<Integer> findAllProductId();
}
