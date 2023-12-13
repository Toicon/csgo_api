package com.csgo.web.request.recharge;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Getter
@Setter
public class EditRechargeChannelPriceItemRequest {

    private BigDecimal price;
    private BigDecimal extraPrice;
    private Integer goodsId;
}
