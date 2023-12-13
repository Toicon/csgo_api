package com.csgo.domain.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;

import java.io.Serializable;

/**
 * Created by admin on 2021/1/4
 */
public enum WithdrawPropItemStatus implements IEnum {
    PENDING("待审核"),
    PASS("通过"),
    REJECT("拒绝"),
    FAKE("已发货"),
    FAILURE("提货失败"),
    AUTO("等待报价"),
    WAITING("等待卖家发货"),
    RETRIEVE("已发货"),
    RECEIPTED("已收货"),
    CANCEL("取消");

    private String description;

    WithdrawPropItemStatus(String description) {
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
