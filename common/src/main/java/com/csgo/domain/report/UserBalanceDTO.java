package com.csgo.domain.report;

import lombok.Data;

import java.math.BigDecimal;


/**
 * 用户余额
 * Created by admin
 */
@Data
public class UserBalanceDTO {
    //余额(V币)
    private BigDecimal balanceAmount;
    //余额(银币)
    private BigDecimal diamondAmount;
}
