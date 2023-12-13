package com.csgo.framework.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author admin
 */
@Getter
@AllArgsConstructor
public enum UserTypeEnums {

    FRONT(0, "前端用户"),
    ADMIN(1, "后端用户"),
    ;

    private final Integer code;

    private final String msg;

}
