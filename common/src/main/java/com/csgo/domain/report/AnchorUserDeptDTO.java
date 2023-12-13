package com.csgo.domain.report;

import lombok.Getter;
import lombok.Setter;

/**
 * 获取所有主播
 */
@Setter
@Getter
public class AnchorUserDeptDTO {

    //后台用户id
    private Integer adminUserId;

    //主播ID
    private Integer userId;


}
