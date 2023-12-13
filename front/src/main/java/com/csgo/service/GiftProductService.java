package com.csgo.service;

import com.csgo.domain.GiftProduct;
import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.mapper.GiftProductMapper;
import com.csgo.mapper.plus.gift.GiftProductPlusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class GiftProductService {

    @Autowired
    private GiftProductMapper giftProductMapper;
    @Autowired
    private GiftProductPlusMapper giftProductPlusMapper;

    public GiftProduct queryById(int id) {
        return giftProductMapper.selectByPrimaryKey(id);
    }

    public List<GiftProduct> getList(GiftProduct giftProduct) {
        return giftProductMapper.getList(giftProduct);
    }

    public List<GiftProduct> getListLt(GiftProduct giftProduct) {
        return giftProductMapper.getListLt(giftProduct);
    }

    public List<GiftProductPlus> findByGiftId(int giftId) {
        return giftProductPlusMapper.findByGiftId(giftId);
    }

    public List<GiftProductPlus> findByGiftProductIds(Collection<Integer> giftProductIds) {
        if (CollectionUtils.isEmpty(giftProductIds)) {
            return new ArrayList<>();
        }
        return giftProductPlusMapper.findByGiftProductIds(giftProductIds);
    }

    public GiftProductPlus getPlus(int id) {
        return giftProductPlusMapper.selectById(id);
    }
}
