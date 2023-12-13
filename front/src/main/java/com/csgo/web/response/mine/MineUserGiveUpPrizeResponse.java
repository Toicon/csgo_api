package com.csgo.web.response.mine;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 扫雷-获取放弃挑战奖励信息
 *
 * @author admin
 */
@Data
public class MineUserGiveUpPrizeResponse {
    /**
     * 第几关(保底奖励为0)
     */
    @ApiModelProperty(notes = "第几关(保底奖励为0)")
    private Integer level;
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

    /**
     * 对应崭新出厂这样的标签
     */
    @ApiModelProperty(value = "对应崭新出厂这样的标签")
    private String exteriorName;

}
