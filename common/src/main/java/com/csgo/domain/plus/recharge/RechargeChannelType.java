package com.csgo.domain.plus.recharge;

/**
 * @author admin
 */
public enum RechargeChannelType {

    ALI_PAY("支付宝支付");

    private final String dis;

    RechargeChannelType(String dis) {
        this.dis = dis;
    }

    public String getDis() {
        return dis;
    }
}
