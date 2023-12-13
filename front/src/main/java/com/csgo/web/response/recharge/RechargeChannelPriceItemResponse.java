package com.csgo.web.response.recharge;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Getter
@Setter
public class RechargeChannelPriceItemResponse {

    private int id;
    private BigDecimal price;
    private BigDecimal extraPrice;
    private BigDecimal pay;
}
