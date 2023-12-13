package com.csgo.domain.plus.message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("user_message_item_record")
public class UserMessageItemRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    @TableField("record_id")
    private Integer recordId;

    @TableField("user_message_id")
    private Integer userMessageId;

    @TableField("img")
    private String img;
}