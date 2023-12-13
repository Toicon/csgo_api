package com.csgo.framework.security.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author admin
 */
@Data
@NoArgsConstructor
public class SecurityUser {

    private Integer type;

    private Integer userId;

    private String name;

    public SecurityUser(Integer type, Integer userId, String name) {
        this.type = type;
        this.userId = userId;
        this.name = name;
    }

}
