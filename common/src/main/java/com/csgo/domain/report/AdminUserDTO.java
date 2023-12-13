package com.csgo.domain.report;

import lombok.Data;

/**
 * 管理员信息
 */
@Data
public class AdminUserDTO {

    //管理员用户ID
    private Integer adminUserId;
    //管理员名称
    private String name;
}
