package com.csgo.web.request.face;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 实名认证认证请求参数
 */
@Data
public class AliRealNameAuthCheckRequest {

    /**
     * 身份证号码
     */
    @ApiModelProperty(notes = "支付宝回调code")
    @NotBlank(message = "code不能为空")
    private String code;
    /**
     * 实名认证申请验证ID
     */
    @ApiModelProperty(notes = "实名认证申请验证ID")
    @NotBlank(message = "验证ID不能为空")
    private String verifyId;
}
