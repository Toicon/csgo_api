package com.csgo.domain.socket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

/**
 * 接收的消息
 * from：消息的来源（一般为发送用户id或者sessionid） 是这条消息的标识 通过这个标志可以找到对应的用户（如果是1v1聊天 这个是找到消息发送者的标志）
 * to：消息的部分目的地（一般为接收用户id或者sessionid）
 * 如果消息发是广播和多播 那前端可以忽略该字段 直接在发送时指定一个固定路由（如：群聊、推送实时消息等）
 * 如果消息发是单播 就要通过to后的标识找到要发送的目的地 再进行拼接
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InMessage {

    private String id = UUID.randomUUID().toString();
    //从哪里来
    private String from;

    //到哪里去（单播必用）
    private String to;

    private Object data;

    private Integer status;

    private Date time = new Date();
}
