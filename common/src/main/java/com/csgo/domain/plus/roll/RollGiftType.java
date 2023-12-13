package com.csgo.domain.plus.roll;

import com.baomidou.mybatisplus.core.enums.IEnum;

import java.io.Serializable;

/**
 * @author admin
 */
public enum RollGiftType implements IEnum {

    NORMAL, INNER, CANCEL;

    @Override
    public Serializable getValue() {
        return this.name();
    }
}
