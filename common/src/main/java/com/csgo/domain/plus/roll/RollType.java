package com.csgo.domain.plus.roll;

import com.baomidou.mybatisplus.core.enums.IEnum;

import java.io.Serializable;

/**
 * @author admin
 */
public enum RollType implements IEnum {

    PERSONAL, OFFICIAL;

    @Override
    public Serializable getValue() {
        return this.name();
    }
}
