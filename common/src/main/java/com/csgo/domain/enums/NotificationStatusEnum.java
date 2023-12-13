package com.csgo.domain.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;

import java.io.Serializable;

/**
 * Created by admin on 2021/1/4
 */
public enum NotificationStatusEnum implements IEnum {
    READ, UNREAD;


    @Override
    public Serializable getValue() {
        return this.name();
    }
}
