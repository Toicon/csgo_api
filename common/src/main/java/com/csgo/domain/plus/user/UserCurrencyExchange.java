package com.csgo.domain.plus.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商城货币兑换记录
 *
 * @author admin
 */
@Data
@TableName("user_currency_exchange")
public class UserCurrencyExchange extends BaseEntity {
    //用户id
    @TableField("user_id")
    private Integer userId;
    //兑换金额(银币)
    @TableField(value = "diamond_amount")
    private BigDecimal diamondAmount;
    //赠送金额(银币)
    @TableField(value = "give_amount")
    private BigDecimal giveAmount;
    //兑换前金额(V币)
    @TableField(value = "front_balance")
    private BigDecimal frontBalance;
    //余额(V币)
    @TableField(value = "balance")
    private BigDecimal balance;
}
