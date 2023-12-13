package com.csgo.domain.plus.gift;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("gift_product_update_record") //道具更新记录表
public class GiftProductUpdateRecord extends BaseEntity {
    @TableField(value = "product_id")
    private int productId;
    @TableField(value = "now_price")
    private BigDecimal nowPrice;
    @TableField(value = "now_origin_price")
    private BigDecimal nowOriginPrice;
    @TableField("update_price")
    private BigDecimal updatePrice;
    @TableField("update_origin_price")
    private BigDecimal updateOriginPrice;
    @TableField("name")
    private String name;
    @TableField("img")
    private String img;
    @TableField("english_name")
    private String englishName;
    @TableField("can_update")
    private boolean canUpdate;
}
