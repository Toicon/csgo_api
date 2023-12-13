package com.csgo.domain.plus.message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.enums.NotificationStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@TableName(value = "message_notification")
@Setter
@Getter
public class MessageNotification {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("user_id")
    private Integer userId;

    @TableField("status")
    private NotificationStatusEnum status;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("ct")
    private Date ct;
}