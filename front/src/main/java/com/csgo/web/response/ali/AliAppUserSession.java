package com.csgo.web.response.ali;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AliAppUserSession {

    @ApiModelProperty(notes = "用户支付宝ID")
    private String userId;

}
