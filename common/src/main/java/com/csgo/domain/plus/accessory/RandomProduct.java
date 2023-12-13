package com.csgo.domain.plus.accessory;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@TableName("sys_random_product")
public class RandomProduct implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("gift_product_id")
    private Integer giftProductId;

    @TableField("lucky_id")
    private Integer luckyId;

    @TableField("probability")
    private Double probability;

    @TableField("img_url")
    private String imgUrl;

    @TableField("price")
    private BigDecimal price;


    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("add_time")
    private java.util.Date addTime;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    private java.util.Date updateTime;


    /*===========================================================================*/

    @TableField(exist = false)
    private String productName;

}
