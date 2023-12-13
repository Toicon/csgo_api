package com.csgo.web.response.fish;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 钓鱼玩法散户奖池
 */
@Getter
@Setter
public class FishUserJackpotResponse {

    private BigDecimal balance;
    private BigDecimal spareBalance;

}
