package com.csgo.modular.bomb.model.front;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author admin
 */
@Data
public class NovBombGamePlayVM {

    @ApiModelProperty(value = "拆除线路")
    @NotNull(message = "拆除线路不能为空")
    private Integer chooseIndex;

}
