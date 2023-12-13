package com.csgo.web.response.role;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by admin on 2021/11/25
 */
@Setter
@Getter
public class RoleAuthorizeResponse {
    private String code;
    private boolean last;
}
