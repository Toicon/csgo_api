package com.csgo.web.request.ali;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 支付宝小程序用户购买商品
 */
@Data
public class AliAppUserBuyRequest {

    @ApiModelProperty(notes = "商品id")
    @NotNull(message = "商品id不能为空")
    private Integer productId;

}
