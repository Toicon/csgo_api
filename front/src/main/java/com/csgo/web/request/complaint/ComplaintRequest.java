package com.csgo.web.request.complaint;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Created by Admin on 2021/6/18
 */
@Setter
@Getter
public class ComplaintRequest {
    //投诉类别
    @NotBlank(message = "type is required")
    private String type;
    //投诉描述
    @NotBlank(message = "description is required")
    private String description;
    //联系电话
    @NotBlank(message = "telephone is required")
    private String telephone;
    //联系邮箱
    @NotBlank(message = "email is required")
    private String email;

    private List<String> imgs;
}
