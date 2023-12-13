package com.csgo.service.stageProperty;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.stageProperty.SearchProductFilterCategoryPlusCondition;
import com.csgo.domain.plus.stageProperty.ProductFilterCategoryPlus;
import com.csgo.mapper.plus.stageProperty.ProductFilterCategoryPlusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author admin
 */
@Service
public class ProductFilterCategoryPlusService {

    @Autowired
    private ProductFilterCategoryPlusMapper productFilterCategoryPlusMapper;

    @Transactional
    public void insert(ProductFilterCategoryPlus productFilterCategoryPlus) {
        productFilterCategoryPlus.setAddTime(new Date());
        productFilterCategoryPlusMapper.insert(productFilterCategoryPlus);
    }

    public Page<ProductFilterCategoryPlus> pagination(SearchProductFilterCategoryPlusCondition condition) {
        return productFilterCategoryPlusMapper.pagination(condition);
    }

    public ProductFilterCategoryPlus get(int id) {
        return productFilterCategoryPlusMapper.selectById(id);
    }

    @Transactional
    public void update(ProductFilterCategoryPlus productFilterCategoryPlus) {
        productFilterCategoryPlusMapper.updateById(productFilterCategoryPlus);
    }

    @Transactional
    public void deleteBach(List<Integer> ids) {
        ids.forEach(id -> {
            productFilterCategoryPlusMapper.deleteById(id);
        });
    }
}
