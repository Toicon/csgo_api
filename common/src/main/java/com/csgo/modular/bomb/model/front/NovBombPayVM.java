package com.csgo.modular.bomb.model.front;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author admin
 */
@Data
@EqualsAndHashCode
public class NovBombPayVM {

    @ApiModelProperty(value = "唯一标识")
    @NotNull(message = "标识不能为空")
    private String uuid;

}
