package com.csgo.modular.giftwish.model.front;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author admin
 */
@Data
public class UserGiftWishVM {

    @ApiModelProperty(value = "礼包ID")
    @NotNull(message = "礼包ID不能为空")
    private Integer giftId;

    @ApiModelProperty(value = "饰品ID")
    @NotNull(message = "饰品不能为空")
    private Integer giftProductId;

}
