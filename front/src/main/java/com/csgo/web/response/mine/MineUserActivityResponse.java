package com.csgo.web.response.mine;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 扫雷-活动管理
 *
 * @author admin
 */
@Data
public class MineUserActivityResponse {

    /**
     * 起始金额
     */
    @ApiModelProperty(notes = "起始金额")
    private BigDecimal minPrice;
    /**
     * 截止金额
     */
    @ApiModelProperty(notes = "截止金额")
    private BigDecimal maxPrice;
    
    /**
     * 货币消耗
     */
    @ApiModelProperty(notes = "货币消耗")
    private BigDecimal extractAmount;
    /**
     * 支付状态(0:未支付，1:已支付)
     */
    @ApiModelProperty(notes = "支付状态(0:未支付，1:已支付)")
    private Integer payState;

    /**
     * 每关奖品信息
     */
    @ApiModelProperty(notes = "每关奖品信息")
    private List<MineUserActivityPrizeItem> prizeItems;

    /**
     * 闯关信息
     */
    @ApiModelProperty(notes = "闯关信息")
    private List<MineUserActivityPassLevelItem> passLevelItems;
}
