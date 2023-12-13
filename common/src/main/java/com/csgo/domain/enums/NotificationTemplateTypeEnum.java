package com.csgo.domain.enums;

import java.io.Serializable;
import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * Created by admin on 2021/1/4
 */
public enum NotificationTemplateTypeEnum implements IEnum {
    RETRIEVE_SUCCESS("0000000000"),
    RETRIEVE_FAILURE("0000000000"),
    DELIVERY_SUCCESS("2000001977786229"),
    GOODS_OFF_SHELVES("7000001523350902"),
    SPILL_PRICE("7000001523350902"),
    FINISH_OPERATION("4000001721299569");

    private String templateCode;

    NotificationTemplateTypeEnum(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    @Override
    public Serializable getValue() {
        return this.name();
    }
}
