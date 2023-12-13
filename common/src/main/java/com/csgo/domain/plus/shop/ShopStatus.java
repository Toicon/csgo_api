package com.csgo.domain.plus.shop;

import com.baomidou.mybatisplus.core.enums.IEnum;

import java.io.Serializable;

/**
 * Created by admin on 2021/1/4
 */
public enum ShopStatus implements IEnum {
    NORMAL, DELETE;


    @Override
    public Serializable getValue() {
        return this.name();
    }
}
