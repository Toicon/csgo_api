package com.csgo.util;

import java.math.BigDecimal;

/**
 * @author admin
 */
public final class GeneralConfigUtil {

    // 0-5=1
    // 6-50=2
    // 51-100=3
    // 100-200=4
    // 200+ =5
    public static int giftGradeG(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) >= 0 && BigDecimal.valueOf(5).compareTo(price) > 0) {
            return 1;
        }
        if (price.compareTo(BigDecimal.valueOf(5)) >= 0 && BigDecimal.valueOf(50).compareTo(price) > 0) {
            return 2;
        }
        if (price.compareTo(BigDecimal.valueOf(50)) >= 0 && BigDecimal.valueOf(100).compareTo(price) > 0) {
            return 3;
        }
        if (price.compareTo(BigDecimal.valueOf(100)) >= 0 && BigDecimal.valueOf(200).compareTo(price) > 0) {
            return 4;
        }
        return 5;
    }
}
