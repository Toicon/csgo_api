package com.csgo.web.request.shop;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 商城货币兑换
 *
 * @author admin
 */
@Data
public class UserCurrencyExchangeRequest {
    @ApiModelProperty(notes = "兑换id")
    @NotNull(message = "兑换id不能为空")
    private Integer configId;
}
