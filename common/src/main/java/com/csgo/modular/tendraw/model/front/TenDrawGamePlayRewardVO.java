package com.csgo.modular.tendraw.model.front;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Data
public class TenDrawGamePlayRewardVO {

    @ApiModelProperty(value = "奖励ID")
    private Integer id;

    @ApiModelProperty(value = "支付价格")
    private BigDecimal playPrice;

    @ApiModelProperty(value = "第几次")
    private Integer playNum;

    @ApiModelProperty(value = "箱子下标")
    private Integer playIndex;

    @ApiModelProperty(value = "奖励饰品ID")
    private Integer rewardProductId;

    @ApiModelProperty(value = "奖励饰品名称")
    private String rewardProductName;

    @ApiModelProperty(value = "奖励商品图片")
    private String rewardProductImg;

    @ApiModelProperty(value = "奖励饰品价格")
    private BigDecimal rewardProductPrice;

    @ApiModelProperty(value = "颜色")
    private Integer color;

}
