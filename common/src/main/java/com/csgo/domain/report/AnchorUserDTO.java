package com.csgo.domain.report;

import lombok.Getter;
import lombok.Setter;

/**
 * 获取所有主播
 */
@Setter
@Getter
public class AnchorUserDTO {

    //主播ID
    private Integer userId;

    //主播名
    private String name;
}
