package com.csgo.mapper.plus.accessory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.csgo.domain.plus.accessory.LuckyProductType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author admin
 */
@Repository
public interface LuckyProductTypeMapper extends BaseMapper<LuckyProductType> {
    default List<LuckyProductType> findList() {
        LambdaQueryWrapper<LuckyProductType> wrapper = Wrappers.lambdaQuery();
        return selectList(wrapper);
    }
}
