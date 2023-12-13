package com.csgo.modular.tendraw.model.front;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Data
public class TenDrawProductVO {

    @ApiModelProperty(value = "饰品ID")
    private Integer productId;

    @ApiModelProperty(value = "饰品名称")
    private String productName;

    @ApiModelProperty(value = "商品图片")
    private String productImg;

    @ApiModelProperty(value = "饰品价格")
    private BigDecimal productPrice;

    @ApiModelProperty(value = "颜色0 1 2")
    private Integer color;

}
