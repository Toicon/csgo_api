package com.csgo.web.request.role;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 2021/11/4
 */
@Setter
@Getter
public class EditRoleAuthorizeRequest {
    private String code;

    private Integer roleId;
}
