package com.csgo.service.accessory;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.accessory.SearchLuckyProductCondition;
import com.csgo.domain.ProductFilterCategoryDO;
import com.csgo.domain.RandomProductDO;
import com.csgo.domain.plus.accessory.LuckyProductDTO;
import com.csgo.domain.plus.lucky.LuckyProductDrawRecordDTO;
import com.csgo.mapper.ProductFilterCategoryMapper;
import com.csgo.mapper.RandomProductMapper;
import com.csgo.mapper.plus.accessory.LuckyProductPlusMapper;
import com.csgo.mapper.plus.lottery.LuckyProductDrawRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class LuckyProductService {

    @Autowired
    private RandomProductMapper randomProductMapper;
    @Autowired
    private ProductFilterCategoryMapper productFilterCategoryMapper;
    @Autowired
    private LuckyProductPlusMapper luckyProductPlusMapper;
    @Autowired
    private LuckyProductDrawRecordMapper luckyProductDrawRecordMapper;

    public List<ProductFilterCategoryDO> getLuckyCategoryList() {
        return productFilterCategoryMapper.getLuckyCategoryList();
    }

    public List<RandomProductDO> randomProductList(Integer luckyId) {
        return randomProductMapper.getRandomProductList(luckyId);
    }

    public LuckyProductDTO get(int id) {
        return luckyProductPlusMapper.get(id);
    }

    public Page<LuckyProductDTO> pagination(SearchLuckyProductCondition condition) {
        return luckyProductPlusMapper.pagination(condition.getPage(), condition);
    }

    public List<LuckyProductDrawRecordDTO> top15(int luckyId) {
        return luckyProductDrawRecordMapper.top15(luckyId);
    }
}
