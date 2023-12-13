package com.csgo.web.response.roll;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Admin on 2021/6/20
 */
@Setter
@Getter
public class RollInviteGiftResponse {
    @ApiModelProperty(notes = "礼物名称", example = "运动手套")
    private String giftName;
    @ApiModelProperty(notes = "礼物图片地址", example = "www.baidu.com")
    private String giftImg;
    @ApiModelProperty(notes = "礼物价格", example = "")
    private BigDecimal giftPrice;
}
