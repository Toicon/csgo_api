package com.csgo.domain.report;

import lombok.Data;

import java.math.BigDecimal;


/**
 * 用户储值收支汇总
 * by admin
 */
@Data
public class UserStoredSummaryDto {
    //日期
    private String date;
    //金额
    private BigDecimal amount;
}
