package com.csgo.mapper.plus.accessory;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.csgo.domain.plus.accessory.LuckyProductDTO;
import com.csgo.domain.plus.accessory.RandomProduct;

/**
 * @author admin
 */
@Repository
public interface RandomLuckyProductPlusMapper extends BaseMapper<RandomProduct> {
    LuckyProductDTO get(int id);

    default List<RandomProduct> findByGiftProductId(Integer originalId) {
        LambdaQueryWrapper<RandomProduct> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RandomProduct::getGiftProductId, originalId);
        return selectList(wrapper);
    }

    List<Integer> findAllProductId();
}
