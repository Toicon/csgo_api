package com.csgo.domain.plus.config;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 添加客服信息
 */
@Getter
@Setter
@TableName("exchange_rate")
public class ExchangeRatePlus {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("exchange_rate")
    private String exchangeRate;
    private Integer flag;
    private Date ct;
    private Date ut;
    @TableField("extract_money")
    private BigDecimal extractMoney;
    @TableField("pay_give_money")
    private BigDecimal payGiveMoney;
    @TableField("spill_price")
    private BigDecimal spillPrice;
    @TableField("first_commission")
    private BigDecimal firstCommission;
    @TableField("second_commission")
    private BigDecimal secondCommission;
    @TableField("lucky_value")
    private Integer luckyValue;
    @TableField("shop_spill_price")
    private BigDecimal shopSpillPrice;
}