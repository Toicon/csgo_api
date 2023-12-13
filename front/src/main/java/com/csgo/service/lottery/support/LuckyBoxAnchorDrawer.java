package com.csgo.service.lottery.support;

import com.csgo.constants.CommonBizCode;
import com.csgo.domain.plus.config.SystemConfigFacade;
import com.csgo.domain.plus.lucky.LotteryDrawRecord;
import com.csgo.domain.plus.lucky.LotteryDrawType;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.framework.exception.BizClientException;
import com.csgo.service.jackpot.BoxAnchorJackpotService;
import com.csgo.support.StandardExceptionCode;
import com.echo.framework.platform.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Slf4j
public class LuckyBoxAnchorDrawer {

    private final LuckyGift gift;
    private final UserPlus player;
    private final SystemConfigFacade config;
    private final BoxAnchorJackpotService boxAnchorJackpotService;
    private final LotteryDrawRecord record;
    private final List<String> messageList;
    private final List<LuckyGiftProduct> luckyGiftProducts;

    private final List<LuckyGiftProduct> specialGiftProducts;

    private final LuckyBoxDrawerContext context;

    public LuckyBoxAnchorDrawer(LuckyGift gift, UserPlus player, SystemConfigFacade config, BoxAnchorJackpotService boxAnchorJackpotService, LuckyBoxDrawerContext context) {
        this.gift = gift;
        this.context = context;
        //需要深拷贝
        this.luckyGiftProducts = gift.getGiftProducts().stream().map(luckyGiftProduct -> {
            LuckyGiftProduct clone = new LuckyGiftProduct();
            BeanUtils.copyProperties(luckyGiftProduct, clone);
            return clone;
        }).collect(Collectors.toList());

        this.specialGiftProducts = gift.getGiftProducts().stream()
                .filter(item -> item.getSpecialState() != null && item.getSpecialState())
                .filter(item -> item.getOutProbability() == 5)
                .map(luckyGiftProduct -> {
                    LuckyGiftProduct clone = new LuckyGiftProduct();
                    BeanUtils.copyProperties(luckyGiftProduct, clone);
                    return clone;
                }).collect(Collectors.toList());

        this.player = player;
        this.config = config;
        this.boxAnchorJackpotService = boxAnchorJackpotService;
        this.record = new LotteryDrawRecord();
        this.messageList = new ArrayList<>();
    }

    public LotteryDrawResult newDraw() {
        List<LuckyGiftProduct> products = this.luckyGiftProducts;
        if (context.mustDrawGold()) {
            products = this.specialGiftProducts;
        }

        if (CollectionUtils.isEmpty(products)) {
            log.error("[开箱抽奖] 系统配置错误 gift:{}", gift.getId());
            throw BizClientException.of(CommonBizCode.COMMON_SYSTEM_CONFIG_ERROR);
        }

        sortAndChangeGiftProduct(products);
        record.setUserId(player.getId());
        record.setLucky(player.getLucky());

        LuckyGiftProduct hitGiftProduct = newDraw(products);

        boolean drawGold = hitGiftProduct.getOutProbability() == 5;
        if (drawGold) {
            context.setDrawGold(true);
        }

        record.setHitGiftProductId(hitGiftProduct.getId());
        record.setMessage(String.join("->", messageList));
        record.setCt(new Date());
        return new LotteryDrawResult(hitGiftProduct, record);
    }

    public LuckyGiftProduct newDraw(List<LuckyGiftProduct> products) {
        int totalWeight = products.stream().map(LuckyGiftProduct::getWeight).reduce(Integer::sum).get();
        int random = (int) (Math.random() * totalWeight) + 1;
        int weight = 0;
        for (LuckyGiftProduct product : products) {
            weight = weight + product.getWeight();
            if (weight < random) {
                continue;
            }

            boolean isHit = false;
            if (context.mustDrawGold()) {
                log.info("[五连出金] 抽奖 productId:{} outProbability:{} price:{}", product.getId(), product.getOutProbability(), product.getPrice());
                boolean preHit = boxAnchorJackpotService.preHit(product.getPrice());
                log.info("[五连出金] 抽奖 productId:{} outProbability:{} price:{} preHit：{}", product.getId(), product.getOutProbability(), product.getPrice(), preHit);
                if (preHit) {
                    isHit = boxAnchorJackpotService.hit(product.getPrice(), player, true);
                }
            } else {
                isHit = boxAnchorJackpotService.hit(product.getPrice(), player, false);
            }

            if (isHit) {
                record.setType(LotteryDrawType.NORMAL);
                return product;
            }
            record.setType(LotteryDrawType.DOWN);
        }
        LuckyGiftProduct product = products.get(products.size() - 1);
        if (context.mustDrawGold()) {
            log.info("[五连出金] 使用默认奖励 productId:{} outProbability:{} price:{}", product.getId(), product.getOutProbability(), product.getPrice());
        }
        boxAnchorJackpotService.hit(product.getPrice(), player, true);
        return product;
    }


    public void sortAndChangeGiftProduct(List<LuckyGiftProduct> products) {
        Collections.sort(products, new Comparator<LuckyGiftProduct>() {
            @Override
            public int compare(LuckyGiftProduct o1, LuckyGiftProduct o2) {
                return o2.getPrice().compareTo(o1.getPrice());
            }
        });
        BigDecimal jackpotBalance;
        BigDecimal warnValue;
        jackpotBalance = boxAnchorJackpotService.getBoxAnchorJackpotBalance();
        warnValue = config.getWarnAnchorValue();
        BigDecimal total = jackpotBalance.add(warnValue);
        BigDecimal x = jackpotBalance.subtract(warnValue);
        BigDecimal adjust = BigDecimal.ONE;
        if (BigDecimal.ZERO.compareTo(x) != 0) {
            adjust = BigDecimal.ONE.add(x.divide(total, 2, BigDecimal.ROUND_DOWN));
        }
        BigDecimal totalPrice = products.stream().map(LuckyGiftProduct::getPrice).reduce(BigDecimal::add).get();
        for (LuckyGiftProduct product : products) {
            if (product.getWeight() > 0) {
                continue;
            }
            //权重计算方法
            int weight = totalPrice.divide(product.getPrice(), 4, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(10000)).intValue();
            if (product.getPrice().compareTo(this.gift.getPrice()) > 0) {
                BigDecimal weight2 = new BigDecimal(weight).multiply(new BigDecimal(1));
                weight = weight2.multiply(adjust).intValue();
            }
            product.setWeight(weight);
        }
    }

}
