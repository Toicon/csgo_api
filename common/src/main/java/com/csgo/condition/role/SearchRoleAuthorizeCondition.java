package com.csgo.condition.role;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.role.RoleAuthorize;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchRoleAuthorizeCondition extends Condition<RoleAuthorize> {
    private String description;
    private String roleName;
}
