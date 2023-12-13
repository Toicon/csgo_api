package com.csgo.domain.plus.zbt;

import com.baomidou.mybatisplus.core.enums.IEnum;

import java.io.Serializable;

/**
 * Created by Admin on 2021/7/18
 */
public enum ZbtGiveOrderStatus implements IEnum {
    PENDING("待处理"), PROCESSING("处理中"), FAILURE("失败"), WAITING("等待买家处理"), COMPLETE("已完成"), CANCEL("已取消");

    private String description;

    ZbtGiveOrderStatus(String description) {
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
