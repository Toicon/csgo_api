package com.csgo.domain.plus.shop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
@TableName("shop_order")
public class ShopOrder {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    @TableField("gift_product_id")
    private Integer giftProductId;
    @TableField("user_id")
    private Integer userId;
    @TableField("before_balance")
    private BigDecimal beforeBalance;
    @TableField("after_balance")
    private BigDecimal afterBalance;
    @TableField("ct")
    private Date ct;
}
