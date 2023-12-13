package com.csgo.modular.common.redis.counter;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author admin
 */
@Getter
@AllArgsConstructor
public enum RedisCounterType {

    REAL_NAME("real-name", "实名认证次数"),
    ;

    private final String code;
    private final String description;

}
