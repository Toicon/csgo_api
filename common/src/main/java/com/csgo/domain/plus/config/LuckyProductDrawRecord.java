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
 * @author admin
 */
@Getter
@Setter
@TableName("lucky_product_draw_record")
public class LuckyProductDrawRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    @TableField("user_id")
    private int userId;
    @TableField("lucky_id")
    private int luckyId;
    private BigDecimal lucky;
    @TableField("product_name")
    private String productName;
    @TableField("price")
    private BigDecimal price;
    @TableField("rate")
    private BigDecimal rate;
    private boolean hit;
    private BigDecimal profit;
    @TableField("hit_product_id")
    private int hitProductId;
    private String message;
    private Date ct;
}
