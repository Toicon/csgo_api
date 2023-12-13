package com.csgo.modular.giftwish.model.front;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Data
public class UserGiftWishRewardVO {

    @ApiModelProperty(value = "饰品ID")
    private Integer giftProductId;

    @ApiModelProperty(value = "饰品名称")
    private String giftProductName;

    @ApiModelProperty(value = "商品图片")
    private String giftProductImg;

    @ApiModelProperty(value = "饰品图标")
    private BigDecimal giftProductPrice;

    @ApiModelProperty(value = "饰品品质")
    private String giftProductGrade;

    @ApiModelProperty(value = "背包ID")
    private Integer userMessageId;

}
