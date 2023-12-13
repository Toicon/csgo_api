package com.csgo.service.fish;

import com.csgo.domain.Gift;
import com.csgo.domain.plus.blind.BlindBoxPlus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author admin
 */
@Getter
@Setter
public class LuckyGift {

    private BigDecimal price; //单个礼包价格
    private final BigDecimal threshold;
    private final int id;
    private final List<LuckyGiftProduct> giftProducts;
    private BigDecimal balance;
    private BigDecimal diamondBalance;

    public LuckyGift(Gift gift, List<LuckyGiftProduct> giftProducts, BigDecimal balance, BigDecimal diamondBalance) {
        this.price = gift.getPrice();
        this.threshold = gift.getThreshold();
        this.id = gift.getId();
        this.giftProducts = giftProducts;
        this.balance = balance;
        this.diamondBalance = diamondBalance;
    }

    public LuckyGift(BlindBoxPlus box, List<LuckyGiftProduct> giftProductList) {
        this.price = box.getPrice();
        this.id = box.getId();
        this.threshold = BigDecimal.ZERO;
        this.giftProducts = giftProductList;
    }

    public int getId() {
        return id;
    }

    public BigDecimal getThreshold() {
        return threshold;
    }
}
