package com.csgo.domain.plus.complaint;

import com.baomidou.mybatisplus.core.enums.IEnum;

import java.io.Serializable;

public enum ComplaintStatus implements IEnum {
    UNCHECKED("未查看"), CHECKED("已查看");
    private String description;

    ComplaintStatus(String description) {
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
