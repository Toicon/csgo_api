package com.csgo.web.request.face;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 实名认证Check返回
 */
@Data
@NoArgsConstructor
public class AliRealNameCheckResponse {

    @ApiModelProperty(value = "实名认证是否通过")
    private Boolean checkPass = Boolean.FALSE;

    @ApiModelProperty(value = "奖励金额")
    private BigDecimal money;

    public AliRealNameCheckResponse(Boolean checkPass) {
        this.checkPass = checkPass;
    }

}
