package com.csgo.support.ZBT;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 2021/7/25
 */
@Setter
@Getter
public class ItemPriceInfoRequest {
    private Integer appId;

    private String[] marketHashNameList;
}
