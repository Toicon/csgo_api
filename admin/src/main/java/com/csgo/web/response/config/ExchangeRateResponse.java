package com.csgo.web.response.config;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class ExchangeRateResponse {

    private Integer id;
    private String exchangeRate;
    private Integer flag;
    private Date ct;
    private Date ut;
    private BigDecimal extractMoney;
    private BigDecimal payGiveMoney;
    private BigDecimal spillPrice;
    private BigDecimal firstCommission;
    private BigDecimal secondCommission;
    private Integer luckyValue;
    private BigDecimal shopSpillPrice;
}
