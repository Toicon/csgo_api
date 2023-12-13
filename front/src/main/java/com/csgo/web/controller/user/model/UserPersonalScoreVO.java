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
public class UserPersonalScoreVO {

    @ApiModelProperty(value = "主键ID", required = false)
    private Integer userId;

    @ApiModelProperty(value = "累计领取红包奖励")
    private BigDecimal redEnvelopReceiveMoney = BigDecimal.ZERO;

    @ApiModelProperty(value = "累计领取cdk奖励")
    private BigDecimal cdkReceiveMoney = BigDecimal.ZERO;

}
