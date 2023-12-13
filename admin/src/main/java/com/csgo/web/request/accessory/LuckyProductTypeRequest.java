package com.csgo.web.request.accessory;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author admin
 */
@Getter
@Setter
public class LuckyProductTypeRequest {
    @NotBlank(message = "名称不能为空")
    private String name;
    @NotBlank(message = "图片不能为空")
    private String img;
    private Integer sortId;
}
