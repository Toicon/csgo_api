package com.csgo.domain.plus.role;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@TableName(value = "role")
public class Role {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("`name`")
    private String name;

    @TableField("content")
    private String content;

    @TableField("data_scope")
    private String dataScope;

    @TableField("ct")
    private Date ct;

    @TableField("ut")
    private Date ut;
}
