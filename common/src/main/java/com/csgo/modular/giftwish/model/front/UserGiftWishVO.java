package com.csgo.modular.giftwish.model.front;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author admin
 */
@Data
public class UserGiftWishVO {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    @ApiModelProperty(value = "许愿ID")
    private Integer id;

    @ApiModelProperty(value = "礼包ID")
    private Integer giftId;

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

    @ApiModelProperty(value = "心愿状态 0进行中 1已领取 2取消")
    private Integer state;

    @JsonIgnore
    private Integer wishCurrent;

    @JsonIgnore
    private Integer wishTotal;

    @ApiModelProperty(value = "心愿百分比")
    private BigDecimal wishPercent;

    @ApiModelProperty(value = "饰品选择人数")
    private Integer giftProductWishCount;

    public BigDecimal getWishPercent() {
        BigDecimal percent;
        if (wishTotal <= wishCurrent) {
            return HUNDRED;
        }
        percent = BigDecimal.valueOf(wishCurrent)
                .divide(BigDecimal.valueOf(wishTotal), 4, RoundingMode.DOWN)
                .multiply(HUNDRED);
        return percent.setScale(2, RoundingMode.DOWN);
    }

}
