package com.csgo.web.request.hee;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author admin
 */
@Data
public class HeeConfirmPayRequest {

    /**
     * 确认支付短信验证码
     */
    @ApiModelProperty(notes = "确认支付短信验证码")
    @NotBlank(message = "短信验证码不能为空")
    private String verifyCode;

}
