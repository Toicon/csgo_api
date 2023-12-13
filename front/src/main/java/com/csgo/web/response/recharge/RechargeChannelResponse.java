package com.csgo.web.response.recharge;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author admin
 */
@Getter
@Setter
public class RechargeChannelResponse {

    private int id;
    private String type;
    private String method;
    private List<RechargeChannelPriceItemResponse> priceItems;
}
