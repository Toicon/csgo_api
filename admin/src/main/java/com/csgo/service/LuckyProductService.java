package com.csgo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.GiftProduct;
import com.csgo.domain.LuckyProductDO;
import com.csgo.domain.RandomProductDO;
import com.csgo.mapper.GiftProductMapper;
import com.csgo.mapper.LuckyProductMapper;
import com.csgo.mapper.RandomProductMapper;
import com.csgo.repository.LuckyProductRepository;
import com.csgo.repository.RandomProductRepository;
import com.csgo.support.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LuckyProductService {

    @Autowired
    private LuckyProductMapper luckyProductMapper;
    @Autowired
    private RandomProductMapper randomProductMapper;
    @Autowired
    private RandomProductRepository randomProductRepository;
    @Autowired
    private LuckyProductRepository luckyProductRepository;
    @Autowired
    private GiftProductMapper giftProductMapper;

    public void addBath(List<LuckyProductDO> luckyProductDOS) {
        List<LuckyProductDO> luckyProductDOList = new ArrayList<>();
        for (LuckyProductDO id : luckyProductDOS) {
            LuckyProductDO luckyProductDO = luckyProductMapper.selectByGiftProductId(id.getGiftProductId());
            if (luckyProductDO == null) {
                luckyProductDO = new LuckyProductDO();
                luckyProductDO.setAddTime(new Date());
                luckyProductDO.setUpdateTime(new Date());
                luckyProductDO.setGiftProductId(id.getGiftProductId());
                luckyProductDO.setSortId(99);
                luckyProductDO.setImgUrl(id.getImgUrl());
                luckyProductDO.setIsRecommend(id.getIsRecommend());
                luckyProductDO.setPrice(id.getPrice());
                luckyProductDO.setTypeId(id.getTypeId());
                luckyProductDO.setUpdateTime(new Date());
                luckyProductDO.setAddTime(new Date());
                luckyProductDOList.add(luckyProductDO);
            }
        }
        if (luckyProductDOList.size() > 0) {
            luckyProductRepository.saveAll(luckyProductDOList);
            luckyProductDOList.forEach(item -> {
                List<RandomProductDO> randomProductDOList = randomProductMapper.getRandomByLuckId(0);
                randomProductDOList.forEach(random -> {
                    random.setId(null);
                    random.setLuckyId(item.getId());
                });
                randomProductRepository.saveAll(randomProductDOList);
            });
        }
    }

    public void update(LuckyProductDO luckyProductDO) {
        Optional<LuckyProductDO> optional = luckyProductRepository.findById(luckyProductDO.getId());
        if (optional != null && optional.get() != null) {
            LuckyProductDO luckyProductDO1 = optional.get();
            luckyProductDO1.setSortId(luckyProductDO.getSortId());
            luckyProductDO1.setImgUrl(luckyProductDO.getImgUrl());
            luckyProductDO1.setPrice(luckyProductDO.getPrice());
            luckyProductDO1.setIsRecommend(luckyProductDO.getIsRecommend());
            luckyProductDO1.setTypeId(luckyProductDO.getTypeId());
            luckyProductRepository.saveAndFlush(luckyProductDO1);
        }
    }

    public void deleteBath(List<Integer> ids) {
        for (Integer id : ids) {
            luckyProductRepository.deleteById(id);
        }
    }

    public PageInfo<GiftProduct> pageList(Integer pageNum, Integer pageSize, String keywords, String csgoType, Integer typeId) {
        Page<GiftProduct> page = new Page<>(pageNum, pageSize);
        Page<GiftProduct> luckyProductDOList = giftProductMapper.pageList(page, keywords, csgoType, typeId);
        return new PageInfo<>(luckyProductDOList);
    }

}
