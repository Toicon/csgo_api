package com.csgo.domain.plus.role;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoleAuthorizeDTO {

    private Integer id;

    private String code;

    private Integer roleId;

    private String roleName;

    private String authorizeName;
}
