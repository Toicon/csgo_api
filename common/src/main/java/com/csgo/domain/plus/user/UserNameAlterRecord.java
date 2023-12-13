package com.csgo.domain.plus.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("user_name_alter_record")
public class UserNameAlterRecord {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("user_id")
    private Integer userId;
    @TableField("source_name")
    private String sourceName;
    @TableField("after_name")
    private String afterName;
    @TableField("ct")
    private Date ct;
}