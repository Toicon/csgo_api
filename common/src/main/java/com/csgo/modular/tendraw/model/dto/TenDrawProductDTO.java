package com.csgo.modular.tendraw.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Data
public class TenDrawProductDTO {

    @ApiModelProperty(value = "奖励饰品ID")
    private Integer productId;

    @ApiModelProperty(value = "奖励饰品名称")
    private String productName;

    @ApiModelProperty(value = "奖励商品图片")
    private String productImg;

    @ApiModelProperty(value = "奖励饰品价格")
    private BigDecimal productPrice;

    private Integer weight;

}
