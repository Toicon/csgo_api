package com.csgo.web.response.zbt;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Admin on 2021/7/19
 */
@Setter
@Getter
public class ZbtMarketResponse {

    private String giftProductName;

    private String itemId;

    private BigDecimal autoDeliverPrice;

    private Integer autoDeliverQuantity;

    private BigDecimal manualDeliverPrice;

    private Integer manualQuantity;
}
