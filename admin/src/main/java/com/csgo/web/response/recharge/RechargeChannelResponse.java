package com.csgo.web.response.recharge;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author admin
 */
@Getter
@Setter
public class RechargeChannelResponse {

    private int id;
    private String type;
    private String typeDis;
    private String method;
    private String methodDis;
    private int sortId;
    private boolean hidden;
    private List<BigDecimal> prices;
}
