package com.csgo.web.response.jackpot;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class BattleJackpotResponse {

    private BigDecimal balance;
    private BigDecimal spareBalance;

}
