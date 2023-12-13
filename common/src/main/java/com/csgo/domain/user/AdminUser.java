package com.csgo.domain.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class AdminUser {

    @ApiModelProperty(value = "主键ID", required = false)
    private Integer id;

    @ApiModelProperty(value = "用户类型(0:企业内部人员,1:主播)", required = true)
    private Integer userType;

    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    @ApiModelProperty(value = "密码", required = true)
    private String password;

    @ApiModelProperty(value = "昵称", required = true)
    private String name;

    @ApiModelProperty(value = "性别", required = true)
    private String sex;

    @ApiModelProperty(value = "部门id", required = true)
    private Integer deptId;

    @ApiModelProperty(value = "角色id", required = true)
    private Integer roleId;

    @ApiModelProperty(value = "角色名称", required = true)
    private String roleName;

    @ApiModelProperty(value = "创建时间", required = true)
    private Date ct;

    @ApiModelProperty(value = "修改时间", required = true)
    private Date ut;

    private Integer pageNum;

    private Integer pageSize;

    private Integer total;
}