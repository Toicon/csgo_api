package com.csgo.domain.plus.recharge;

/**
 * @author admin
 */
public enum RechargeMethod {

    WECHAT("微信"), ALI("支付宝"), UNION("云闪付"), UNIONPAY("银联"), ALIAPP("支付宝小程序");

    private final String dis;

    RechargeMethod(String dis) {
        this.dis = dis;
    }

    public String getDis() {
        return dis;
    }
}
