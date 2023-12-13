package com.csgo.modular.bomb.model.front;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Data
public class NovBombGamePlayVO {

    @ApiModelProperty(value = "奖励ID")
    private Integer id;

    @ApiModelProperty(value = "状态(0:进行中，1:已结束)")
    private Integer status;

    @ApiModelProperty(value = "选择线路")
    private Integer chooseIndex;

    @ApiModelProperty(value = "奖励商品id")
    private Integer rewardProductId;

    @ApiModelProperty(value = "奖励商品图")
    private String rewardProductImg;

    @ApiModelProperty(value = "奖励商品名称")
    private String rewardProductName;

    @ApiModelProperty(value = "奖励商品金额")
    private BigDecimal rewardProductPrice;

    @ApiModelProperty(value = "商品下标")
    private Integer rewardProductIndex;

    @ApiModelProperty(value = "饰品标签")
    private String rewardProductExteriorName;

}
