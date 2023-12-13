package com.csgo.service.fish;

import com.csgo.domain.plus.config.SystemConfigFacade;
import com.csgo.domain.plus.lucky.LotteryDrawRecord;
import com.csgo.domain.plus.lucky.LotteryDrawType;
import com.csgo.domain.plus.user.UserPlus;
import com.csgo.service.jackpot.FishAnchorJackpotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class FishAnchorDrawer {

    private final LuckyGift gift;
    private final UserPlus player;
    private final SystemConfigFacade config;
    private final FishAnchorJackpotService fishAnchorJackpotService;
    private final LotteryDrawRecord record;
    private final List<String> messageList;
    private final List<LuckyGiftProduct> luckyGiftProducts;

    public FishAnchorDrawer(LuckyGift gift, UserPlus player, SystemConfigFacade config, FishAnchorJackpotService fishAnchorJackpotService) {
        this.gift = gift;
        //需要深拷贝
        this.luckyGiftProducts = gift.getGiftProducts().stream().map(luckyGiftProduct -> {
            LuckyGiftProduct clone = new LuckyGiftProduct();
            BeanUtils.copyProperties(luckyGiftProduct, clone);
            return clone;
        }).collect(Collectors.toList());
        this.player = player;
        this.config = config;
        this.fishAnchorJackpotService = fishAnchorJackpotService;
        this.record = new LotteryDrawRecord();
        this.messageList = new ArrayList<>();
    }


    public LotteryDrawResult newDraw() {
        List<LuckyGiftProduct> products = this.luckyGiftProducts;
        sortAndChangeGiftProduct(products);
        record.setUserId(player.getId());
        record.setLucky(player.getLucky());
        LuckyGiftProduct hitGiftProduct = newDraw(products);
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
            boolean isHit = fishAnchorJackpotService.hit(product.getPrice(), false);
            if (isHit) {
                record.setType(LotteryDrawType.NORMAL);
                return product;
            }
            record.setType(LotteryDrawType.DOWN);
        }
        LuckyGiftProduct product = products.get(products.size() - 1);
        fishAnchorJackpotService.hit(product.getPrice(), true);
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
        BigDecimal fishValue;
        jackpotBalance = fishAnchorJackpotService.getFishAnchorJackpotBalance();
        fishValue = config.getFishWarnValue();
        BigDecimal total = jackpotBalance.add(fishValue);
        BigDecimal x = jackpotBalance.subtract(fishValue);
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
