package com.csgo.web.request.face;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author admin
 */
@Data
public class TencentRealNameCheckRequest {
    /**
     * 真实姓名
     */
    @ApiModelProperty(notes = "真实姓名")
    @NotBlank(message = "真实姓名不能为空")
    private String name;
    /**
     * 身份证号码
     */
    @ApiModelProperty(notes = "身份证号码")
    @NotBlank(message = "身份证号码不能为空")
    private String idNo;

}
