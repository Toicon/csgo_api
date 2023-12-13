package com.csgo.modular.verify.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author admin
 */
@Data
public class BehaviorRegisterVO {

    private String gt;

    private String challenge;

    private Boolean new_captcha;

    @ApiModelProperty(value = "是否走宕机流程")
    private Boolean offline;

}
