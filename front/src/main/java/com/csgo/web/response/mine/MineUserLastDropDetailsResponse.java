package com.csgo.web.response.mine;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 扫雷-获取奖励信息
 */
@Data
public class MineUserLastDropDetailsResponse {

    /**
     * 第几关
     */
    @ApiModelProperty(notes = "第几关")
    private Integer level;
    /**
     * 地雷格子集合
     */
    @ApiModelProperty(notes = "地雷格子集合")
    private List<Integer> mineLattice;
    /**
     * 非地雷格子集合
     */
    @ApiModelProperty(notes = "非地雷格子集合")
    private List<Integer> nonMineLattice;
    /**
     * 用户选中格子(没选择默认空)
     */
    @ApiModelProperty(notes = "用户选中格子(没选择默认空)")
    private Integer selectLattice;

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
