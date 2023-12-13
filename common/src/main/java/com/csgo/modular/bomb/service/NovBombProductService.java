package com.csgo.modular.bomb.service;

import cn.hutool.core.collection.CollUtil;
import com.csgo.constants.CommonBizCode;
import com.csgo.framework.exception.BizServerException;
import com.csgo.framework.util.BeanCopyUtil;
import com.csgo.mapper.plus.gift.GiftProductPlusMapper;
import com.csgo.modular.bomb.domain.NovBombConfigDO;
import com.csgo.modular.bomb.model.NovBombSettingVO;
import com.csgo.modular.bomb.model.front.NovBombProductVO;
import com.csgo.modular.bomb.service.system.NovBombSystemConfigService;
import com.csgo.modular.bomb.support.NovBombPriceHelper;
import com.csgo.modular.product.model.front.ProductSimpleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NovBombProductService {

    private final GiftProductPlusMapper giftProductPlusMapper;
    private final NovBombSystemConfigService novBombSystemConfigService;

    public List<NovBombProductVO> getProduct(NovBombConfigDO config, BigDecimal price) {
        NovBombSettingVO setting = novBombSystemConfigService.getSetting();
        NovBombPriceHelper priceHelper = new NovBombPriceHelper(setting, 5, config.getProductPriceParams(), price);
        List<ProductSimpleVO> productList = getRandomProduct(priceHelper.getPriceList());

        List<NovBombProductVO> voList = BeanCopyUtil.mapList(productList, NovBombProductVO.class);
        for (int i = 0; i < voList.size(); i++) {
            int num = i + 1;
            Integer productRate = null;
            if (num == 1) {
                productRate = config.getRate1();
            } else if (num == 2) {
                productRate = config.getRate2();
            } else if (num == 3) {
                productRate = config.getRate3();
            } else if (num == 4) {
                productRate = config.getRate4();
            } else if (num == 5) {
                productRate = config.getRate5();
            }
            NovBombProductVO item = voList.get(i);
            item.setProductIndex(i);
            item.setProductRate(productRate);
        }
        voList.sort(Comparator.comparing(NovBombProductVO::getProductPrice).reversed());
        return voList;
    }

    public List<ProductSimpleVO> getRandomProduct(List<BigDecimal> priceList) {
        if (CollUtil.isEmpty(priceList)) {
            log.error("[模拟拆弹] 随机饰品价格列表不能为空");
            throw BizServerException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
        }
        List<ProductSimpleVO> list = priceList.parallelStream().map(price -> {
            ProductSimpleVO productByNearPrice = giftProductPlusMapper.getProductByNearPrice(price);
            if (productByNearPrice == null) {
                log.error("[模拟拆弹] 价格过低，找不到相应饰品");
                throw BizServerException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
            }
            return productByNearPrice;
        }).collect(Collectors.toList());
        if (list.size() != priceList.size()) {
            log.error("[模拟拆弹] 随机饰品价格列表不能为空");
            throw BizServerException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
        }
        return list;
    }

}
