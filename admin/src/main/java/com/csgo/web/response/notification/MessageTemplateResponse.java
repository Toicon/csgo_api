package com.csgo.web.response.notification;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by admin on 2021/4/29
 */
@Getter
@Setter
public class MessageTemplateResponse {

    private Integer id;

    private String type;

    private String title;

    private String content;
}
