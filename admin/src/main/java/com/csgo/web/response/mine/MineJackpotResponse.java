package com.csgo.web.response.mine;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 扫雷活动
 *
 * @author admin
 */
@Getter
@Setter
public class MineJackpotResponse {

    private BigDecimal balance;
    private BigDecimal spareBalance;

}
