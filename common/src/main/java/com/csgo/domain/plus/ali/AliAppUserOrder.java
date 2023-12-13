package com.csgo.domain.plus.ali;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付宝小程序用户订单
 *
 */
@Data
@TableName("ali_app_user_order")
public class AliAppUserOrder extends BaseEntity {
    //用户id
    @TableField(value = "user_id")
    @JsonIgnore
    private Integer userId;
    //订单号
    @ApiModelProperty(notes = "订单号")
    @TableField(value = "order_num")
    private String orderNum;
    //商品名称
    @ApiModelProperty(notes = "商品名称")
    @TableField(value = "product_name")
    private String productName;
    //商品图片
    @ApiModelProperty(notes = "商品图片")
    @TableField(value = "product_img")
    private String productImg;
    //商品价格
    @ApiModelProperty(notes = "商品价格")
    @TableField("product_price")
    private BigDecimal productPrice;
    //订单状态（ 0：待发货 1：已发货）
    @TableField(value = "order_status")
    @JsonIgnore
    private Integer orderStatus;
}
