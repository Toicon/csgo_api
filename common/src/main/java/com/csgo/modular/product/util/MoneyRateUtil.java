package com.csgo.modular.product.util;

import com.csgo.framework.util.BigDecimalUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author admin
 */
public class MoneyRateUtil {

    public static BigDecimal getUsdPrice(BigDecimal price) {
        if (price != null && BigDecimalUtil.greaterThanZero(price)) {
            return price.divide(new BigDecimal("6.5"), 2, RoundingMode.DOWN);
        }
        return BigDecimal.ZERO;
    }

}
