package com.csgo.web.request.notification;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * Created by admin on 2021/4/29
 */
@Getter
@Setter
public class EditMessageTemplateRequest {

    @NotBlank(message = "type is required")
    private String type;

    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "content is required")
    private String content;
}
