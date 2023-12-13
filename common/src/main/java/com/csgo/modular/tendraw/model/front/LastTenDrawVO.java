package com.csgo.modular.tendraw.model.front;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author admin
 */
@Data
@EqualsAndHashCode
public class LastTenDrawVO {

    @ApiModelProperty(value = "是否存在未完成的游戏")
    private Boolean existGame;

    @ApiModelProperty(value = "游戏ID")
    private Integer gameId;

    @ApiModelProperty(value = "饰品预览")
    private List<TenDrawProductVO> productList;

    @ApiModelProperty(value = "奖励预览")
    private List<TenDrawGamePlayRewardVO> rewardList;

    @ApiModelProperty(value = "支付价格")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "重抽支付价格")
    private BigDecimal retryPrice;

    @ApiModelProperty(value = "抽奖次数")
    private Integer playTimes;

    @ApiModelProperty(value = "总抽奖次数")
    private Integer maxPlayTimes;

    private Integer blueRate;
    private Integer redRate;
    private Integer yellowRate;

}
