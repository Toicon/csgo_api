package com.csgo.domain.plus.shop;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.plus.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
@TableName("shop")
public class Shop extends BaseEntity {
    @TableField("gift_product_id")
    private Integer giftProductId;
    @TableField("stock")
    private Integer stock;
    @TableField("hidden")
    private Boolean hidden;
    @TableField("status")
    private ShopStatus status;
}
