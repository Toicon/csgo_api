package com.csgo.web.response.fish;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 钓鱼玩法测试奖池
 */
@Getter
@Setter
public class FishAnchorJackpotResponse {

    private BigDecimal balance;
    private BigDecimal spareBalance;

}
