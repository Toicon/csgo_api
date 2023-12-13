package com.csgo.modular.product.logic;

import cn.hutool.core.collection.CollUtil;
import com.beust.jcommander.internal.Lists;
import com.csgo.domain.plus.gift.GiftProductPlus;
import com.csgo.mapper.plus.gift.GiftProductPlusMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GiftProductQueryLogic {

    private final GiftProductPlusMapper giftProductPlusMapper;

    public Map<Integer, GiftProductPlus> mapByIds(List<Integer> productIds) {
        List<GiftProductPlus> list = listByIds(productIds);
        return list.stream().collect(Collectors.toMap(GiftProductPlus::getId, Function.identity()));
    }

    public List<GiftProductPlus> listByIds(List<Integer> productIds) {
        if (CollUtil.isEmpty(productIds)) {
            return Lists.newArrayList();
        }
        return giftProductPlusMapper.selectBatchIds(productIds);
    }

}
