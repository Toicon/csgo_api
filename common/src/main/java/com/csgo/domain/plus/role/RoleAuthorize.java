package com.csgo.domain.plus.role;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@TableName(value = "role_authorize")
public class RoleAuthorize {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("`code`")
    private String code;

    @TableField("role_id")
    private Integer roleId;
}
