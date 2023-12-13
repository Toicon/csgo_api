package com.csgo.web.request.code;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class ReceiveActivationCodeRequest {
    @ApiModelProperty(notes = "CDK")
    private String cdKey;
}
