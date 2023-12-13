package com.csgo.service.accessory.support;

import com.csgo.domain.plus.accessory.LuckyProductDTO;
import com.csgo.domain.plus.gift.RandomGiftProductDTO;
import com.csgo.domain.plus.user.UserPlus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Getter
@Setter
public class LuckyProductDrawerCondition {

    private UserPlus player;
    private BigDecimal pay;
    private LuckyProductDTO luckyProduct;
    private RandomGiftProductDTO randomGiftProductDTO;
    private int luckyId;
    private BigDecimal luckyValue;
    private BigDecimal balance;
    private BigDecimal diamondBalance;

}
