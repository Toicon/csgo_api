package com.csgo.domain.plus.zbt;

import com.baomidou.mybatisplus.core.enums.IEnum;

import java.io.Serializable;

/**
 * Created by admin on 2021/1/4
 */
public enum ZbtGiveOrderType implements IEnum {
    AUTO("自动发货"), MANUAL("手动发货");
    private String description;

    ZbtGiveOrderType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public Serializable getValue() {
        return this.name();
    }
}
