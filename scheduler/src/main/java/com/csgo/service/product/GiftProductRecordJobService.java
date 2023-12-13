package com.csgo.service.product;

import com.csgo.domain.plus.gift.GiftPlus;
import com.csgo.domain.plus.gift.GiftProductRecordPlus;
import com.csgo.domain.plus.gift.RandomGiftProductDTO;
import com.csgo.mapper.plus.gift.GiftPlusMapper;
import com.csgo.mapper.plus.gift.GiftProductPlusMapper;
import com.csgo.mapper.plus.gift.GiftProductRecordPlusMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author admin
 */
@Slf4j
@Service
public class GiftProductRecordJobService {


    @Autowired
    private GiftProductRecordPlusMapper giftProductRecordPlusMapper;

    @Autowired
    private GiftPlusMapper giftPlusMapper;

    @Autowired
    private GiftProductPlusMapper giftProductPlusMapper;

    public List<GiftProductRecordPlus> findByGiftIds(List<Integer> giftIds, String isDefault) {
        if (CollectionUtils.isEmpty(giftIds)) {
            return new ArrayList<>();
        }
        return giftProductRecordPlusMapper.findByGiftIds(giftIds, isDefault);
    }
    
    /**
     * 获取价格权重礼包列表
     *
     * @return
     */
    public List<GiftPlus> findProbabilityGiftList() {
        return giftPlusMapper.findProbabilityGiftList();
    }


    /**
     * 修改价格权重物品信息
     *
     * @param giftId
     */
    @Transactional
    public void updateGiftProductByPrice(Integer giftId) {
        if (giftId == null) {
            return;
        }
        List<GiftProductRecordPlus> productRecordPlusList = giftProductRecordPlusMapper.findByGiftId(giftId);
        if (!CollectionUtils.isEmpty(productRecordPlusList)) {
            for (GiftProductRecordPlus item : productRecordPlusList) {
                //价格权重:根据价格获取商品id
                RandomGiftProductDTO randomGiftProductDTO = giftProductPlusMapper.getRandomGiftProductByPrice(item.getProbabilityPrice());
                if (randomGiftProductDTO == null) {
                    log.error("获取不到礼包id：{}，价格：{}", giftId, item.getProbabilityPrice());
                    continue;
                }
                Integer giftProductId = randomGiftProductDTO.getGiftProductId();
                item.setGiftProductId(giftProductId);
                item.setUt(new Date());
                giftProductRecordPlusMapper.updateById(item);
            }
        }

    }

}
