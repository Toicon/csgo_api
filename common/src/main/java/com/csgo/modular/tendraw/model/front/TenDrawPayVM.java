package com.csgo.modular.tendraw.model.front;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author admin
 */
@Data
@EqualsAndHashCode
public class TenDrawPayVM {

    @ApiModelProperty(value = "唯一标识")
    @NotNull(message = "标识不能为空")
    private String uuid;

}
