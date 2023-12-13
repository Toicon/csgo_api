package com.csgo.service.accessory;

import com.csgo.domain.plus.accessory.LuckyProductType;
import com.csgo.mapper.plus.accessory.LuckyProductTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author admin
 */
@Service
public class LuckyProductTypeService {

    @Autowired
    private LuckyProductTypeMapper typeMapper;

   public List<LuckyProductType> findList() {
        return typeMapper.findList();
    }

    @Transactional
    public void insert(LuckyProductType type) {
        type.setCt(new Date());
        typeMapper.insert(type);
    }

    @Transactional
    public void delete(int id) {
        typeMapper.deleteById(id);
    }

    public LuckyProductType get(int id) {
        return typeMapper.selectById(id);
    }

    @Transactional
    public void update(LuckyProductType type) {
        type.setUt(new Date());
        typeMapper.updateById(type);
    }
}
