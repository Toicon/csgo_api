package com.csgo.domain.plus.shop;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商城货币兑换配置
 *
 * @author admin
 */
@Data
@TableName("shop_currency_config")
public class ShopCurrencyConfig extends BaseEntity {
    //金额(银币)
    @ApiModelProperty(notes = "兑换额度")
    @TableField(value = "diamond_amount")
    private BigDecimal diamondAmount;
    //额外赠送百分率
    @ApiModelProperty(notes = "额外赠送百分率")
    @TableField(value = "give_rate", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal giveRate;
}
