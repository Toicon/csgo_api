package com.csgo.modular.product.util;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author admin
 */
public class GiftProductWithdrawUtil {

    public static String createNewOutTradeNo() {
        return "csgo_" + System.currentTimeMillis() + RandomStringUtils.randomNumeric(6);
    }

}
