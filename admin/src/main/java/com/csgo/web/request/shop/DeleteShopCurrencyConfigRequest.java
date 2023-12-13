package com.csgo.web.request.shop;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author admin
 */
@Data
public class DeleteShopCurrencyConfigRequest {
    @ApiModelProperty(notes = "id")
    @NotNull(message = "id不能为空")
    private Integer id;
}
