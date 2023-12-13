package com.csgo.modular.tendraw.service;

import cn.hutool.core.collection.CollUtil;
import com.csgo.constants.CommonBizCode;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.framework.exception.BizServerException;
import com.csgo.modular.tendraw.domain.TenDrawGameDO;
import com.csgo.modular.tendraw.domain.TenDrawProductDO;
import com.csgo.modular.tendraw.service.system.TenDrawJackpotService;
import com.csgo.modular.tendraw.service.system.TenDrawSystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenDrawLotteryService {

    private final TenDrawJackpotService tenDrawJackpotService;
    private final TenDrawSystemConfigService tenDrawSystemConfigService;

    public TenDrawProductDO draw(UserPlus player, TenDrawGameDO game, List<TenDrawProductDO> productList) {
        if (CollUtil.isEmpty(productList)) {
            log.error("[十连开箱] 系统奖品配置错误 gameId:{}", game.getId());
            throw BizServerException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
        }
        fillWeight(player, game, productList);

        return doDraw(player, game, productList);
    }

    private TenDrawProductDO doDraw(UserPlus player, TenDrawGameDO game, List<TenDrawProductDO> products) {
        int totalWeight = products.stream().map(TenDrawProductDO::getWeight).reduce(0, Integer::sum);
        int random = (int) (Math.random() * totalWeight) + 1;

        int weight = 0;
        for (TenDrawProductDO product : products) {
            weight = weight + product.getWeight();
            if (weight < random) {
                continue;
            }
            boolean isHit = tenDrawJackpotService.hit(player, product.getProductPrice());
            log.info("[十连Draw] 奖品 gameId:{} productId:{} price:{} isHit:{} random:{}", game.getId(), product.getId(), product.getProductPrice(), isHit, random);
            if (isHit) {
                return product;
            }
        }
        TenDrawProductDO product = products.get(products.size() - 1);
        log.info("[十连Draw] 奖品 gameId:{} 使用默认奖励 productId:{} price:{}", game.getId(), product.getId(), product.getProductPrice());
        return product;
    }

    public void fillWeight(UserPlus player, TenDrawGameDO game, List<TenDrawProductDO> productList) {
        productList.sort((o1, o2) -> o2.getProductPrice().compareTo(o1.getProductPrice()));

        BigDecimal jackpotBalance = tenDrawJackpotService.getJackpot(player);
        BigDecimal warnValue = tenDrawSystemConfigService.getWarnValue();

        BigDecimal total = jackpotBalance.add(warnValue);
        BigDecimal x = jackpotBalance.subtract(warnValue);
        BigDecimal adjust = BigDecimal.ONE;
        if (BigDecimal.ZERO.compareTo(x) != 0) {
            adjust = BigDecimal.ONE.add(x.divide(total, 2, RoundingMode.DOWN));
        }

        BigDecimal totalPrice = productList.stream().map(TenDrawProductDO::getProductPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        for (TenDrawProductDO product : productList) {
            //权重计算方法
            int weight = totalPrice.divide(product.getProductPrice(), 4, RoundingMode.DOWN).multiply(new BigDecimal(10000)).intValue();
            if (product.getProductPrice().compareTo(game.getSumAmount()) > 0) {
                BigDecimal newWeight = new BigDecimal(weight).multiply(new BigDecimal(1));
                weight = newWeight.multiply(adjust).intValue();
            }
            product.setWeight(weight);
        }
    }

    private TenDrawProductDO getRandomProduct(List<TenDrawProductDO> productList) {
        int index = RandomUtils.nextInt(0, productList.size());
        return productList.get(index);
    }

}
