package com.csgo.web.request.face;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 实名认证用户信息
 */
@Data
public class AliRealNameAuthInfo {
    /**
     * 是否实名认证(0:否,1:是)
     */
    private Integer realNameState;


    @ApiModelProperty(value = "奖励金额")
    private BigDecimal money = new BigDecimal("0.00");

}
