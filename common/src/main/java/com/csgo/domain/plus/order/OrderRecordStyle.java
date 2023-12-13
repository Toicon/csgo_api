package com.csgo.domain.plus.order;

import com.baomidou.mybatisplus.core.enums.IEnum;

import java.io.Serializable;

/**
 * @author admin
 */
public enum OrderRecordStyle implements IEnum {

    ALI_PAY("支付宝支付");

    private String description;

    OrderRecordStyle(String description) {
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
