package com.csgo.web.request.mine;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 扫雷-活动闯关
 *
 * @author admin
 */
@Data
public class MineUserConfigPriceRequest {

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
}
