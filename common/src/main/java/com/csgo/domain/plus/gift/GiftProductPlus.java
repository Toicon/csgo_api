package com.csgo.domain.plus.gift;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.enums.GiftProductStatusEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("gift_product") //所有道具表
public class GiftProductPlus {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String type;
    private BigDecimal price;

    @TableField("origin_amount")
    private BigDecimal originAmount;
    @TableField("rmb_price")
    private BigDecimal rmbPrice;
    private String name;
    @TableField("origin_name")
    private String originName;
    private String img;
    @TableField("status")
    private GiftProductStatusEnum status;
    @TableField("zbt_stock")
    private Integer zbtStock;
    @TableField("created_at")
    private Date createdAt;
    @TableField("updated_at")
    private Date updatedAt;
    @TableField("gift_id")
    private Integer giftId;
    @TableField("within_probability")
    private String withinProbability;
    @TableField("out_probability")
    private int outProbability;
    @TableField("game_name")
    private String gameName;
    private String isdefault;
    private String content;
    private String grade;
    @TableField("englishName")
    private String englishName;
    @TableField("itemId")
    private String itemId;
    @TableField("csgo_type")
    private String csgoType;
    @TableField("exterior_name")
    private String exteriorName;
    @TableField("template_id")
    private Integer templateId;
    @TableField("source_type")
    private Integer sourceType;
    @TableField("product_kind")
    private Integer productKind;
    @TableField("zbt_item_id")
    private String zbtItemId;
}
