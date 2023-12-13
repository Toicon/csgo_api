package com.csgo.mapper.plus.gift;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csgo.domain.enums.YesOrNoEnum;
import com.csgo.domain.plus.gift.GiftProductRecordPlus;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author admin
 */
@Repository
public interface GiftProductRecordPlusMapper extends BaseMapper<GiftProductRecordPlus> {

    default List<GiftProductRecordPlus> findByGiftProductIds(Collection<Integer> giftProductIds, String isDefault) {
        LambdaQueryWrapper<GiftProductRecordPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(GiftProductRecordPlus::getGiftProductId, giftProductIds);
        if (isDefault != null) {
            wrapper.eq(GiftProductRecordPlus::getIsdefault, isDefault);
        }
        return selectList(wrapper);
    }

    default List<GiftProductRecordPlus> findByGiftIds(List<Integer> giftIds, String isDefault) {
        LambdaQueryWrapper<GiftProductRecordPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(GiftProductRecordPlus::getGiftId, giftIds);
        if (isDefault != null) {
            wrapper.eq(GiftProductRecordPlus::getIsdefault, isDefault);
        }
        return selectList(wrapper);
    }

    List<Integer> findAllProductId();

    default GiftProductRecordPlus findByGiftIdAndProductId(Integer giftId, Integer giftProductId) {
        LambdaQueryWrapper<GiftProductRecordPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GiftProductRecordPlus::getGiftId, giftId);
        wrapper.eq(GiftProductRecordPlus::getGiftProductId, giftProductId);
        wrapper.last("limit 1");
        return selectOne(wrapper);
    }

    default int getCountSpecialStateByGiftId(Integer giftId) {
        LambdaQueryWrapper<GiftProductRecordPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GiftProductRecordPlus::getGiftId, giftId);
        wrapper.eq(GiftProductRecordPlus::getSpecialState, YesOrNoEnum.YES.getCode());
        return selectCount(wrapper);
    }

    default List<GiftProductRecordPlus> findByGiftId(Integer giftId) {
        LambdaQueryWrapper<GiftProductRecordPlus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GiftProductRecordPlus::getGiftId, giftId);
        wrapper.isNotNull(GiftProductRecordPlus::getProbabilityPrice);
        return selectList(wrapper);
    }

    List<Integer> findExistGiftKeyGiftId();

}
