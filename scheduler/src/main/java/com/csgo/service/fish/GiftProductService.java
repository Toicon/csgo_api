package com.csgo.service.fish;

import com.csgo.domain.plus.gift.GiftProductPlus;
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
    private GiftProductPlusMapper giftProductPlusMapper;

    public List<GiftProductPlus> findByGiftProductIds(Collection<Integer> giftProductIds) {
        if (CollectionUtils.isEmpty(giftProductIds)) {
            return new ArrayList<>();
        }
        return giftProductPlusMapper.findByGiftProductIds(giftProductIds);
    }

}
