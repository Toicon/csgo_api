package com.csgo.web.support;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author admin
 */
@Getter
@Setter
public class UserInfo {

    private Integer id;
    private String username;
    private String password;
    private String name;
    private Integer roleId;
    private String roleName;
    private Date ct;
    private Date ut;
    private List<String> authorizes;
    private BigDecimal capRestrictions;
}
