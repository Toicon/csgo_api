package com.csgo.domain.plus.user;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@TableName("admin_user")
public class AdminUserPlus {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 用户类型(0:企业内部人员,1:主播)
     */
    private Integer userType;
    private String username;
    private String password;
    private String name;
    private String sex;
    @TableField("dept_id")
    private Integer deptId;
    @TableField(value = "role_id", updateStrategy = FieldStrategy.IGNORED)
    private Integer roleId;
    @TableField("role_name")
    private String roleName;
    @TableField("cap_restrictions")
    private BigDecimal capRestrictions;
    private boolean frozen;
    private String phone;
    private Date ct;
    private Date ut;
}
