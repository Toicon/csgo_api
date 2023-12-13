package com.csgo.support.ZBT;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 2021/7/20
 */
@Setter
@Getter
public class ZBTSteamResult {
    private ZBTSteamInfo data;
    private Integer errorCode;
    private Object errorData;
    private String errorMsg;
    private Boolean success;
}
