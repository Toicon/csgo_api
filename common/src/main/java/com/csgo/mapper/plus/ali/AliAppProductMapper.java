package com.csgo.mapper.plus.ali;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.ali.SearchAliAppProductCondition;
import com.csgo.domain.enums.YesOrNoEnum;
import com.csgo.domain.plus.ali.AliAppProduct;
import org.springframework.stereotype.Repository;

/**
 * 支付宝小程序商品表
 */
@Repository
public interface AliAppProductMapper extends BaseMapper<AliAppProduct> {
    default Page<AliAppProduct> findAppProduct(SearchAliAppProductCondition condition) {
        LambdaQueryWrapper<AliAppProduct> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(AliAppProduct::getDisplayState, YesOrNoEnum.NO.getCode());
        wrapper.orderByDesc(AliAppProduct::getCreateDate);
        return selectPage(condition.getPage(), wrapper);
    }
}
