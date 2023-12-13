package com.csgo.web.controller.user.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Data
@NoArgsConstructor
public class UserPersonalRechargeVO {

    @ApiModelProperty(value = "主键ID", required = false)
    private Integer userId;

    @ApiModelProperty(value = "今日累计充值")
    private BigDecimal rechargeMoneyToday = BigDecimal.ZERO;

    @ApiModelProperty(value = "本周累计充值")
    private BigDecimal rechargeMoneyWeek = BigDecimal.ZERO;

    @ApiModelProperty(value = "本月累计充值")
    private BigDecimal rechargeMoneyMonth = BigDecimal.ZERO;

}
