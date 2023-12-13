package com.csgo.modular.product.logic;

import com.csgo.mapper.plus.gift.GiftProductRecordPlusMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GiftProductRecordLogic {

    private final GiftProductRecordPlusMapper giftProductRecordPlusMapper;

    public Set<Integer> findExistGiftKeyGiftId() {
        return new HashSet<>(giftProductRecordPlusMapper.findExistGiftKeyGiftId());
    }

}
