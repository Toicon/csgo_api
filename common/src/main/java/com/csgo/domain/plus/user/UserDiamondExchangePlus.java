package com.csgo.domain.plus.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户银币兑换V币记录
 *
 * @author admin
 */
@Data
@TableName("user_diamond_exchange")
public class UserDiamondExchangePlus extends BaseEntity {
    //用户id
    @TableField(value = "user_id")
    private Integer userId;
    //兑换类型(0:保持不变，1：全部兑换为V币)
    @TableField(value = "exchange_type")
    private Integer exchangeType;
    //兑换金额(银币)
    @TableField(value = "diamond_balance")
    private BigDecimal diamondBalance;
    //兑换前余额(V币)
    @TableField(value = "front_balance")
    private BigDecimal frontBalance;
    //余额(V币)
    @TableField(value = "balance")
    private BigDecimal balance;
}
