package com.csgo.domain.plus.message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("user_message_record")
public class UserMessageRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    @TableField("user_id")
    private Integer userId;

    @TableField("num")
    private String num;

    @TableField("source")
    private String source;

    @TableField("operation")
    private String operation;

    @TableField("ct")
    private Date ct;
}