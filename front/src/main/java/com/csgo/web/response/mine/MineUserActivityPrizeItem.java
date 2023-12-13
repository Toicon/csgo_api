package com.csgo.web.response.mine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 扫雷-活动管理
 *
 * @author admin
 */
@Data
public class MineUserActivityPrizeItem {

    /**
     * 第几关
     */
    @ApiModelProperty(notes = "第几关")
    private Integer level;
    /**
     * 奖品商品id
     */
    @JsonIgnore
    private Integer giftProductId;
    /**
     * 奖品商品名称
     */
    @ApiModelProperty(notes = "奖品商品名称")
    private String giftProductName;
    /**
     * 奖品商品图片
     */
    @ApiModelProperty(notes = "奖品商品图片")
    private String giftProductImg;
    /**
     * 奖品商品价格
     */
    @ApiModelProperty(notes = "奖品商品价格")
    private BigDecimal giftProductPrice;

    /**
     * 奖品标签
     */
    @ApiModelProperty(notes = "奖品标签")
    private String exteriorName;

}
