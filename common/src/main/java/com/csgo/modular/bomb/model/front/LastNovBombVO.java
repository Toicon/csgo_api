package com.csgo.modular.bomb.model.front;

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
public class LastNovBombVO {

    @ApiModelProperty(value = "是否存在未完成的游戏")
    private Boolean existGame;

    @ApiModelProperty(value = "场次ID")
    private Integer configId;

    @ApiModelProperty(value = "最低值")
    private BigDecimal lowPrice;

    @ApiModelProperty(value = "最高值")
    private BigDecimal highPrice;

    @ApiModelProperty(value = "最大次数")
    private Integer maxTimes;

    @ApiModelProperty(value = "困难次数")
    private Integer difficultyTimes;

    @ApiModelProperty(value = "饰品列表")
    private List<NovBombProductVO> productList;

    @ApiModelProperty(value = "游戏详情")
    private NovBombGamePlayVO play;

}
