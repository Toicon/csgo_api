package com.csgo.domain.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;

import java.io.Serializable;

/**
 * Created by admin on 2021/1/4
 */
public enum WithdrawPropStatus implements IEnum {
    PENDING("待审核"),
    PASS("通过"),
    FAKE("已发货"),
    REJECT("拒绝"),
    AUTO("自动发货"),
    FAILURE("提货失败");

    private String description;

    WithdrawPropStatus(String description) {
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
