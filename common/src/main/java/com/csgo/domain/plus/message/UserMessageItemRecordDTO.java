package com.csgo.domain.plus.message;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserMessageItemRecordDTO {

    private int id;
    @TableField("record_id")
    private Integer recordId;
    @TableField("user_message_id")
    private Integer userMessageId;
    @TableField("img")
    private String img;
    @TableField("out_probability")
    private int outProbability;
}