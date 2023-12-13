package com.csgo.mapper.plus.stageProperty;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.stageProperty.SearchProductFilterCategoryPlusCondition;
import com.csgo.domain.plus.stageProperty.ProductFilterCategoryPlus;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/**
 * @author admin
 */
@Repository
public interface ProductFilterCategoryPlusMapper extends BaseMapper<ProductFilterCategoryPlus> {

    default Page<ProductFilterCategoryPlus> pagination(SearchProductFilterCategoryPlusCondition condition) {
        LambdaQueryWrapper<ProductFilterCategoryPlus> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.hasText(condition.getName())) {
            wrapper.like(ProductFilterCategoryPlus::getName, condition.getName());
        }
        wrapper.eq(ProductFilterCategoryPlus::getParentKey, "0");
        wrapper.orderByDesc(ProductFilterCategoryPlus::getSortId, ProductFilterCategoryPlus::getAddTime);
        return selectPage(condition.getPage(), wrapper);
    }
}
