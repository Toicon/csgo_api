package com.csgo.domain.plus.authorize;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@TableName(value = "authorize")
public class Authorize {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("`code`")
    private String code;

    @TableField("parent_code")
    private String parentCode;

    @TableField("description")
    private String description;

    @TableField("ct")
    private Date ct;

    @TableField("ut")
    private Date ut;
}
