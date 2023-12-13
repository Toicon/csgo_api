package com.csgo.domain.plus.accessory;

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
@TableName("sys_lucky_product")
public class LuckyProduct {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    @TableField("gift_product_id")
    private Integer giftProductId;
    @TableField("sort_id")
    private Integer sortId;
    @TableField("img_url")
    private String imgUrl;
    @TableField("price")
    private BigDecimal price;
    @TableField("type_id")
    private Integer typeId;
    @TableField("is_recommend")
    private Integer isRecommend;
    @TableField("add_time")
    private Date addTime;
    @TableField("update_time")
    private Date updateTime;
}
