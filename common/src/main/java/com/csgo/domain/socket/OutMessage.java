package com.csgo.domain.socket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/*
 * 发送的消息
 * from：前端拿到发送的可以做一些事
 * 没有to字段 因为已经发送到接收者 所以没有意义
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutMessage {

    private String from;

    private Object data;

    private Integer status;

    private Date time = new Date();
}
