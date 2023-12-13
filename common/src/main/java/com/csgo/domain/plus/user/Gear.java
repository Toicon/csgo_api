package com.csgo.domain.plus.user;

import java.math.BigDecimal;

/**
 * @author admin
 * 测试账号档位，不同档位开箱权重所乘以的系数不同
 */
public enum Gear {
    GEAR1(1, new BigDecimal(1)), GEAR2(2, new BigDecimal(1.5)), GEAR3(3, new BigDecimal(3)), GEAR4(4, new BigDecimal(6)), GEAR5(5, new BigDecimal(10));

    private final int gear;

    private final BigDecimal ratio;

    Gear(int gear, BigDecimal ratio) {
        this.gear = gear;
        this.ratio = ratio;
    }

    public int getGear() {
        return gear;
    }

    public BigDecimal getRatio() {
        return ratio;
    }

    public static Gear of(int gear) {
        for (Gear value : values()) {
            if (value.gear == gear) {
                return value;
            }
        }
        return Gear.GEAR1;
    }
}
