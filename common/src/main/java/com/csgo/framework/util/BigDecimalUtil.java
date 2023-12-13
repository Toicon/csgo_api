package com.csgo.framework.util;

import java.math.BigDecimal;

/**
 * @author admin
 */
public final class BigDecimalUtil {

    private BigDecimalUtil() {

    }

    /**
     * 判断num1是否小于num2
     *
     * @param num1
     * @param num2
     * @return num1小于num2返回true
     */
    public static boolean lessThan(BigDecimal num1, BigDecimal num2) {
        return num1.compareTo(num2) < 0;
    }

    public static boolean lessThanZero(BigDecimal num1) {
        return lessThan(num1, BigDecimal.ZERO);
    }

    /**
     * 判断num1是否小于等于num2
     *
     * @param num1
     * @param num2
     * @return num1小于或者等于num2返回true
     */
    public static boolean lessEqual(BigDecimal num1, BigDecimal num2) {
        return (num1.compareTo(num2) < 0) || (num1.compareTo(num2) == 0);
    }

    public static boolean lessEqualZero(BigDecimal num1) {
        return lessEqual(num1, BigDecimal.ZERO);
    }

    /**
     * 判断num1是否大于num2
     *
     * @param num1
     * @param num2
     * @return num1大于num2返回true
     */
    public static boolean greaterThan(BigDecimal num1, BigDecimal num2) {
        return num1.compareTo(num2) > 0;
    }

    public static boolean greaterThanZero(BigDecimal num1) {
        return greaterThan(num1, BigDecimal.ZERO);
    }

    /**
     * 判断num1是否大于等于num2
     *
     * @param num1
     * @param num2
     * @return num1大于或者等于num2返回true
     */
    public static boolean greaterEqual(BigDecimal num1, BigDecimal num2) {
        return (num1.compareTo(num2) > 0) || (num1.compareTo(num2) == 0);
    }

    public static boolean greaterEqualZero(BigDecimal num1) {
        return greaterEqual(num1, BigDecimal.ZERO);
    }

    /**
     * 判断num1是否等于num2
     *
     * @param num1
     * @param num2
     * @return num1等于num2返回true
     */
    public static boolean equal(BigDecimal num1, BigDecimal num2) {
        return num1.compareTo(num2) == 0;
    }

    public static boolean equalZero(BigDecimal num1) {
        return equal(num1, BigDecimal.ZERO);
    }

}
