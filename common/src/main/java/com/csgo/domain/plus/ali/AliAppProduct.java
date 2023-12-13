package com.csgo.domain.plus.ali;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付宝小程序商品表
 *
 * @author admin
 */
@Data
@TableName("ali_app_product")
public class AliAppProduct extends BaseEntity {
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
    //库存量
    @ApiModelProperty(notes = "库存量")
    @TableField(value = "stock")
    private Integer stock;
    //是否隐藏(0:否,1:是)
    @TableField(value = "display_state")
    @JsonIgnore
    private Integer displayState;
}
