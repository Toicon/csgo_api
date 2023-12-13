package com.csgo.web.request.hee;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author admin
 */
@Data
public class HeeCancelAuthRequest {

    /**
     * 绑卡id
     */
    @ApiModelProperty(notes = "绑卡id")
    @NotNull(message = "绑卡id不能为空")
    private Integer signId;

}
