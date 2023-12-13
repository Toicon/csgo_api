package com.csgo.modular.tendraw.model.front;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author admin
 */
@Data
public class TenDrawGamePlayVM {

    @ApiModelProperty(value = "箱子编号")
    @NotNull(message = "箱子编号不能为空")
    private Integer playIndex;

}
