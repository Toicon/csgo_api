package com.csgo.web.response.jackpot;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author admin
 */
@Getter
@Setter
public class JackpotResponse {

    private BigDecimal jackpot;
    private BigDecimal rate;
    private BigDecimal luckyJackpot;
    private BigDecimal formulateJackpot;

    public void calculate() {
         this.luckyJackpot = jackpot.multiply(rate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
         this.formulateJackpot = jackpot.subtract(luckyJackpot);
    }

}
