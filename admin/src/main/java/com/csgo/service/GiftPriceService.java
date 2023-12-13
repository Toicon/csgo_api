package com.csgo.service;

import com.csgo.domain.GiftPrice;
import com.csgo.mapper.GiftPriceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GiftPriceService {

    @Autowired
    private GiftPriceMapper giftPriceMapper;

    public GiftPrice getOne(GiftPrice giftProduct) {
        return giftPriceMapper.getOne(giftProduct);
    }

    public List<GiftPrice> getList(GiftPrice giftProduct) {
        return giftPriceMapper.getList(giftProduct);
    }

}
