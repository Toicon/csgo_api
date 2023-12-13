package com.csgo.domain.plus.message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.enums.NotificationTemplateTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@TableName(value = "message_template")
public class MessageTemplate {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("type")
    private NotificationTemplateTypeEnum type;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("ct")
    private Date ct;

    @TableField("ut")
    private Date ut;

}