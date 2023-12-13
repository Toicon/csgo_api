package com.csgo.modular.tendraw.model.front;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Data
public class TenDrawGamePlayVO {

    @ApiModelProperty(value = "抽奖次数")
    private Integer playTimes;

    @ApiModelProperty(value = "总抽奖次数")
    private Integer maxPlayTimes;

    @ApiModelProperty(value = "最后一次抽奖")
    private Boolean lastGame;

    @ApiModelProperty(value = "重试金额")
    private BigDecimal retryPrice;

    @ApiModelProperty(value = "0进行中 1已结束")
    private Integer state;

    @ApiModelProperty(value = "奖励")
    private TenDrawGamePlayRewardVO reward;

}
