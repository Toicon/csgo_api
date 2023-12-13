package com.csgo.service.accessory;

import com.csgo.domain.plus.accessory.LuckyProductType;
import com.csgo.mapper.plus.accessory.LuckyProductTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Admin on 2021/6/11
 */
@Service
public class LuckyProductTypeService {
    @Autowired
    private LuckyProductTypeMapper luckyProductTypeMapper;

    public List<LuckyProductType> find() {
        return luckyProductTypeMapper.selectList(null);
    }
}
