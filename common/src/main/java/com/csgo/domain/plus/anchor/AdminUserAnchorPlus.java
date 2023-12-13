package com.csgo.domain.plus.anchor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 主播部门配置
 */
@Data
@TableName("admin_user_anchor")
public class AdminUserAnchorPlus {

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "后台用户id")
    @TableField("admin_user_id")
    private Integer adminUserId;

    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    private Integer userId;

    @ApiModelProperty(value = "创建时间")
    private Date ct;

    @ApiModelProperty(value = "修改时间")
    private Date ut;

}