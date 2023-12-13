package com.csgo.support.ZBT;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Admin on 2021/7/25
 */
@Setter
@Getter
public class ZBTProductPrice {
    private List<PriceInfo> data;

    private Integer errorCode;

    private Object errorData;

    private String errorMsg;

    private Boolean success;
}
