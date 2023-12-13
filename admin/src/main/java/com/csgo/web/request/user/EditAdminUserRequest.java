package com.csgo.web.request.user;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Getter
@Setter
public class EditAdminUserRequest {
    /**
     * 用户类型(0:企业内部人员,1:主播)
     */
    private Integer userType;
    private String username;
    private String password;
    private String name;
    private String phone;
    private Integer roleId;
    private Integer deptId;
    private BigDecimal capRestrictions;
}
