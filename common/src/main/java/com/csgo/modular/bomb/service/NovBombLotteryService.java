package com.csgo.modular.bomb.service;

import cn.hutool.core.collection.CollUtil;
import com.csgo.constants.CommonBizCode;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.framework.exception.BizServerException;
import com.csgo.modular.bomb.domain.NovBombGameDO;
import com.csgo.modular.bomb.domain.NovBombGamePlayDO;
import com.csgo.modular.bomb.domain.NovBombProductDO;
import com.csgo.modular.bomb.mapper.NovBombGameProductMapper;
import com.csgo.modular.bomb.service.system.NovBombJackpotService;
import com.csgo.modular.bomb.service.system.NovBombSystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NovBombLotteryService {

    private final NovBombGameProductMapper novBombGameProductMapper;

    private final NovBombJackpotService novBombJackpotService;
    private final NovBombSystemConfigService novBombSystemConfigService;

    public NovBombProductDO draw(UserPlus player, NovBombGameDO game, NovBombGamePlayDO play, Integer chooseIndex, List<NovBombProductDO> productList) {
        if (CollUtil.isEmpty(productList)) {
            log.error("[模拟拆弹][draw] 系统奖品配置错误 gameId:{}", game.getId());
            throw BizServerException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
        }

        BigDecimal weightPrice = getWeightPrice(play);
        fillWeight(player, weightPrice, productList);

        NovBombProductDO reward = doDraw(player, game, productList);
        // saveIndex(chooseIndex, productList, reward);
        return reward;
    }

    private static BigDecimal getWeightPrice(NovBombGamePlayDO play) {
        BigDecimal weightPrice = play.getPrice();
        if (play.getRewardProductPrice() != null) {
            weightPrice = weightPrice.add(play.getRewardProductPrice());
        }
        return weightPrice;
    }

    private NovBombProductDO doDraw(UserPlus player, NovBombGameDO game, List<NovBombProductDO> products) {
        int totalWeight = products.stream().map(NovBombProductDO::getWeight).reduce(0, Integer::sum);
        int random = (int) (Math.random() * totalWeight) + 1;

        int weight = 0;
        for (NovBombProductDO product : products) {
            weight = weight + product.getWeight();
            if (weight < random) {
                continue;
            }
            boolean isHit = novBombJackpotService.hit(player, product.getProductPrice());
            log.info("[模拟拆弹][draw] 奖品 gameId:{} productId:{} price:{} isHit:{} random:{}", game.getId(), product.getId(), product.getProductPrice(), isHit, random);
            if (isHit) {
                return product;
            }
        }
        NovBombProductDO product = products.get(products.size() - 1);
        log.info("[模拟拆弹][draw] 奖品 gameId:{} 使用默认奖励 productId:{} price:{}", game.getId(), product.getId(), product.getProductPrice());
        return product;
    }

    public void fillWeight(UserPlus player, BigDecimal weightPrice, List<NovBombProductDO> productList) {
        productList.sort((o1, o2) -> o2.getProductPrice().compareTo(o1.getProductPrice()));

        BigDecimal jackpotBalance = novBombJackpotService.getJackpot(player);
        BigDecimal warnValue = novBombSystemConfigService.getWarnValue();

        BigDecimal total = jackpotBalance.add(warnValue);
        BigDecimal x = jackpotBalance.subtract(warnValue);
        BigDecimal adjust = BigDecimal.ONE;
        if (BigDecimal.ZERO.compareTo(x) != 0) {
            adjust = BigDecimal.ONE.add(x.divide(total, 2, RoundingMode.DOWN));
        }

        BigDecimal totalPrice = productList.stream().map(NovBombProductDO::getProductPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        for (NovBombProductDO product : productList) {
            // 权重计算方法
            int weight = totalPrice.divide(product.getProductPrice(), 4, RoundingMode.DOWN).multiply(new BigDecimal(10000)).intValue();
            if (product.getProductPrice().compareTo(weightPrice) > 0) {
                BigDecimal newWeight = new BigDecimal(weight).multiply(new BigDecimal(1));
                weight = newWeight.multiply(adjust).intValue();
            }
            product.setWeight(weight);
        }
    }

    private void saveIndex(Integer chooseIndex, List<NovBombProductDO> productList, NovBombProductDO reward) {
        reward.setProductIndex(chooseIndex);
        novBombGameProductMapper.updateById(reward);

        List<Integer> indexList = IntStream.range(0, 5).boxed().filter(i -> !i.equals(chooseIndex)).collect(Collectors.toList());
        Collections.shuffle(indexList);
        List<NovBombProductDO> remaningList = productList.stream().filter(i -> !i.getId().equals(reward.getId())).collect(Collectors.toList());
        for (int i = 0; i < remaningList.size(); i++) {
            NovBombProductDO entity = remaningList.get(i);
            entity.setProductIndex(indexList.get(i));
            novBombGameProductMapper.updateById(entity);
        }
    }

}
