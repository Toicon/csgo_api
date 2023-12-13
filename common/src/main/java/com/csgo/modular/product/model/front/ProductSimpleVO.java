package com.csgo.modular.product.model.front;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Data
@NoArgsConstructor
public class ProductSimpleVO {

    @ApiModelProperty(value = "饰品ID")
    private Integer productId;

    @ApiModelProperty(value = "饰品名称")
    private String productName;

    @ApiModelProperty(value = "商品图片")
    private String productImg;

    @ApiModelProperty(value = "饰品价格")
    private BigDecimal productPrice;

    @ApiModelProperty(value = "饰品标签")
    private String productExteriorName;


}
