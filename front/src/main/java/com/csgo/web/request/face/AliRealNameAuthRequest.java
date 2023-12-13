package com.csgo.web.request.face;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 实名认证地址请求参数
 */
@Data
public class AliRealNameAuthRequest {
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
    /**
     * 用户 ID ，用户的唯一标识（不要带有特殊字符）
     */
    private Integer userId;

    /**
     * 实名认证申请验证ID
     */
    private String verifyId;
}
