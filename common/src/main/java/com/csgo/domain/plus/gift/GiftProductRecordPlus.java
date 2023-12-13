package com.csgo.domain.plus.gift;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品记录
 */
@Getter
@Setter
@TableName("gift_product_record")
public class GiftProductRecordPlus {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("gift_id")
    private Integer giftId;
    @TableField("gift_product_id")
    private Integer giftProductId;
    @TableField("within_probability")
    private String withinProbability;
    @TableField("out_probability")
    private String outProbability;
    @TableField("show_probability")
    private String showProbability;
    @TableField("weight")
    private int weight;
    private String isdefault;
    private Date ct;
    private Date ut;
    private Integer num;
    private Boolean specialState;
    private BigDecimal probabilityPrice;
}
