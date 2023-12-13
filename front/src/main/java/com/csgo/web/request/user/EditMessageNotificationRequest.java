package com.csgo.web.request.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by admin on 2021/4/29
 */
@Getter
@Setter
public class EditMessageNotificationRequest {

    @NotNull(message = " user is required")
    private Integer userId;

    @NotBlank(message = "type is required")
    private String type;

    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "content is required")
    private String content;
}
