package com.csgo.domain.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;

import java.io.Serializable;

/**
 * Created by admin on 2021/1/4
 */
public enum WithdrawDeliveryMethod implements IEnum {
    ZBT,
    IG,
    YY;

    @Override
    public Serializable getValue() {
        return this.name();
    }
}
