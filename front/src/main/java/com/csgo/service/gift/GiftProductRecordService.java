package com.csgo.service.gift;

import com.csgo.domain.GiftProductRecord;
import com.csgo.domain.plus.gift.GiftProductRecordPlus;
import com.csgo.mapper.GiftProductRecordMapper;
import com.csgo.mapper.plus.gift.GiftProductRecordPlusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author admin
 */
@Service
public class GiftProductRecordService {

    @Autowired
    private GiftProductRecordMapper giftProductRecordMapper;
    @Autowired
    private GiftProductRecordPlusMapper giftProductRecordPlusMapper;

    public List<GiftProductRecordPlus> findByGiftProductIds(Collection<Integer> giftProductIds, String isDefault) {
        if (CollectionUtils.isEmpty(giftProductIds)) {
            return new ArrayList<>();
        }
        return giftProductRecordPlusMapper.findByGiftProductIds(giftProductIds, isDefault);
    }

    public List<GiftProductRecordPlus> findByGiftIds(List<Integer> giftIds, String isDefault) {
        if (CollectionUtils.isEmpty(giftIds)) {
            return new ArrayList<>();
        }
        return giftProductRecordPlusMapper.findByGiftIds(giftIds, isDefault);
    }

    public List<GiftProductRecord> find(Integer id, String isDefault) {
        GiftProductRecord gp = new GiftProductRecord();
        gp.setGiftId(id);
        if (isDefault != null) {
            gp.setIsdefault(isDefault);
        }
        return giftProductRecordMapper.getList(gp);
    }
}
