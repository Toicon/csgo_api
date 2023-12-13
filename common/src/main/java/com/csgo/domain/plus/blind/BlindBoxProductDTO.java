package com.csgo.domain.plus.blind;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableField;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlindBoxProductDTO {
    private Integer id;
    @TableField("blind_box_id")
    private Integer blindBoxId;
    @TableField("gift_product_id")
    private Integer giftProductId;
    @TableField("img_url")
    private String imgUrl;
    private BigDecimal price;
    private BigDecimal probability;
    private int weight;
    @TableField("out_probability")
    private int outProbability;
    @TableField("add_time")
    private Date addTime;
    @TableField("update_time")
    private Date updateTime;
    private String productName;
}
