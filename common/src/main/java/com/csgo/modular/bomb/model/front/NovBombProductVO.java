package com.csgo.modular.bomb.model.front;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Data
public class NovBombProductVO {

    @ApiModelProperty(value = "饰品ID")
    private Integer productId;

    @ApiModelProperty(value = "饰品名称")
    private String productName;

    @ApiModelProperty(value = "商品图片")
    private String productImg;

    @ApiModelProperty(value = "饰品价格")
    private BigDecimal productPrice;

    @ApiModelProperty(value = "掉落概率")
    private Integer productRate;

    @ApiModelProperty(value = "商品下标")
    private Integer productIndex;

    @ApiModelProperty(value = "饰品标签")
    private String productExteriorName;

}
