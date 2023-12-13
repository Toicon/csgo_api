package com.csgo.web.response.fish;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 钓鱼玩法-主动起竿
 */
@Data
public class FishUserGiveUpResponse {
    //奖励名称
    @ApiModelProperty(notes = "奖励名称")
    private String giftProductName;
    //奖励图片
    @ApiModelProperty(notes = "奖励图片")
    private String giftProductImage;
    //奖励价格
    @ApiModelProperty(notes = "奖励价格")
    private BigDecimal giftProductPrice;
    //颜色等级
    @ApiModelProperty(notes = "颜色等级")
    private Integer outProbability;
}
