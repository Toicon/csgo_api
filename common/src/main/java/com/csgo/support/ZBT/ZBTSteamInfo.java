package com.csgo.support.ZBT;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Admin on 2021/7/20
 */
@Setter
@Getter
public class ZBTSteamInfo {
    private Integer appId;

    private Integer checkStatus;

    private List<CheckStatusInfo> statusList;

    private UserSteamInfo steamInfo;
}
