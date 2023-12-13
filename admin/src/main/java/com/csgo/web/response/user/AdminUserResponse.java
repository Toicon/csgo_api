package com.csgo.web.response.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class AdminUserResponse {
    private Integer id;
    /**
     * 用户类型(0:企业内部人员,1:主播)
     */
    private Integer userType;
    private String username;
    private String password;
    private String phone;
    private String name;
    private String sex;
    private Integer deptId;
    private String deptName;
    private Integer roleId;
    private String roleName;
    private BigDecimal capRestrictions;
    private boolean frozen;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date ct;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date ut;
    @ApiModelProperty(notes = "是否实名认证(0:否,1:是)")
    private Integer realNameState;
}
