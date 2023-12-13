package com.csgo.web.request.shop;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author admin
 */
@Data
public class AddShopCurrencyConfigRequest {
    @ApiModelProperty(notes = "银币面额")
    @NotNull(message = "银币面额不能为空")
    private BigDecimal diamondAmount;
    @ApiModelProperty(notes = "额外赠送百分率")
    private BigDecimal giveRate;
}
