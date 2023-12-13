package com.csgo.domain.plus.shop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
@TableName("shop_buy_record")
public class ShopBuyRecord {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    @TableField("gift_product_id")
    private int giftProductId;
    @TableField("user_id")
    private int userId;
    @TableField("stock")
    private int stock;
    @TableField("ct")
    private Date ct;
}
