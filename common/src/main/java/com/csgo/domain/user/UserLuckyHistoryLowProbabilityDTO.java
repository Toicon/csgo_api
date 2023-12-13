package com.csgo.domain.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Data
@NoArgsConstructor
public class UserLuckyHistoryLowProbabilityDTO {

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "饰品ID")
    private Integer productId;

    @ApiModelProperty(value = "饰品名称")
    private String productName;

    @ApiModelProperty(value = "商品图片")
    private String productImg;

    @ApiModelProperty(value = "饰品价格")
    private BigDecimal productPrice;

    @ApiModelProperty(value = "概率  百分比", required = true)
    private Integer probability;

}
