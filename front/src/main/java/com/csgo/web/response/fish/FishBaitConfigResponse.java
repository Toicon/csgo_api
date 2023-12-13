package com.csgo.web.response.fish;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 钓鱼玩法-鱼饵配置信息
 */
@Data
public class FishBaitConfigResponse {
    /**
     * 鱼饵id
     */
    @ApiModelProperty(notes = "鱼饵id")
    private Integer baitId;
    //鱼饵名称
    @ApiModelProperty(notes = "鱼饵名称")
    private String baitName;
    //鱼饵图片
    @ApiModelProperty(notes = "鱼饵图片")
    private String baitImg;
    //鱼饵背景图片地址
    @ApiModelProperty(notes = "鱼饵背景图片地址")
    private String baitBgImg;
    //支付倍数
    @ApiModelProperty(notes = "支付倍数")
    private BigDecimal payRatio;
    //开箱概率
    @ApiModelProperty(notes = "开箱概率")
    private Integer openBox;
}
