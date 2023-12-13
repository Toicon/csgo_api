package com.csgo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.domain.GiftProduct;
import com.csgo.domain.RandomProductDO;
import com.csgo.mapper.GiftProductMapper;
import com.csgo.mapper.RandomProductMapper;
import com.csgo.repository.RandomProductRepository;
import com.csgo.support.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RandomProductService {

    @Autowired
    private RandomProductMapper randomProductMapper;

    @Autowired
    private RandomProductRepository randomProductRepository;

    @Autowired
    private GiftProductMapper giftProductMapper;

    public void addBath(List<RandomProductDO> dos) {
        List<RandomProductDO> productList = new ArrayList<>();
        for (RandomProductDO random : dos) {
            RandomProductDO randomProductDO = randomProductMapper.selectByGiftProductId(random.getGiftProductId(), random.getLuckyId());
            if (randomProductDO == null) {
                randomProductDO = new RandomProductDO();
                randomProductDO.setAddTime(new Date());
                randomProductDO.setUpdateTime(new Date());
                randomProductDO.setGiftProductId(random.getGiftProductId());
                randomProductDO.setProbability(0.00);
                randomProductDO.setImgUrl(random.getImgUrl());
                randomProductDO.setPrice(random.getPrice());
                randomProductDO.setLuckyId(random.getLuckyId());
                productList.add(randomProductDO);
            }
        }
        if (!productList.isEmpty()) {
            randomProductRepository.saveAll(productList);
        }
    }

    public void update(RandomProductDO randomProductDO) {
        Optional<RandomProductDO> optional = randomProductRepository.findById(randomProductDO.getId());
        if (optional.isPresent()) {
            RandomProductDO productDO = optional.get();
            productDO.setImgUrl(randomProductDO.getImgUrl());
            productDO.setPrice(randomProductDO.getPrice());
            productDO.setProbability(randomProductDO.getProbability());
            randomProductRepository.saveAndFlush(productDO);
        }
    }

    public void deleteBath(List<Integer> ids) {
        for (Integer id : ids) {
            randomProductRepository.deleteById(id);
        }
    }

    public PageInfo<GiftProduct> pageList(Integer pageNum, Integer pageSize, String keywords, String csgoType, Integer luckyId) {
        Page<GiftProduct> page = new Page<>(pageNum, pageSize);
        Page<GiftProduct> randomProductDOList = giftProductMapper.randomPageList(page, keywords, csgoType, luckyId);
        return new PageInfo<>(randomProductDOList);
    }
}
