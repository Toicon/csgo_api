package com.csgo.support.ZBT;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by admin on 2021/5/25
 */
@Setter
@Getter
public class ZBTOrderInfo {
    private ZBTOrderData data;

    private Boolean success;

    private Integer errorCode;

    private String errorMsg;
}
