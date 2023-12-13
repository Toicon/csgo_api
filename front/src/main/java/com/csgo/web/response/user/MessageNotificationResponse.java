package com.csgo.web.response.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by admin on 2021/4/29
 */
@Getter
@Setter
public class MessageNotificationResponse {

    private Integer id;
    private Integer userId;
    private String title;
    private String content;
    private String status;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date ct;
}
