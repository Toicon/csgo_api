package com.csgo.web.response.fish;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 起竿返回奖励信息
 */
@Data
public class FishActivityGiveUpResponse {
    /**
     * 奖品名称
     */
    @ApiModelProperty(notes = "奖品名称")
    private String giftProductName;
    /**
     * 奖品图片
     */
    @ApiModelProperty(notes = "奖品图片")
    private String giftProductImg;
    /**
     * 奖品商品价格
     */
    @ApiModelProperty(value = "奖品商品价格")
    private BigDecimal giftProductPrice;

}
