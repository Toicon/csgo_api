package com.csgo.modular.tendraw.service;

import cn.hutool.core.collection.CollUtil;
import com.csgo.constants.CommonBizCode;
import com.csgo.framework.exception.BizClientException;
import com.csgo.mapper.plus.gift.GiftProductPlusMapper;
import com.csgo.modular.product.model.front.ProductSimpleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenDrawProductService {

    private final GiftProductPlusMapper giftProductPlusMapper;

    public List<ProductSimpleVO> getRandomProduct(List<BigDecimal> priceList) {
        if (CollUtil.isEmpty(priceList)) {
            log.error("[十连开箱] 随机饰品价格列表不能为空");
            throw BizClientException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
        }
        return priceList.parallelStream().map(giftProductPlusMapper::getProductByNearPrice).collect(Collectors.toList());
    }

}
