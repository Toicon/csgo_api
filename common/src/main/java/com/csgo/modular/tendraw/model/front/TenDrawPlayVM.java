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
public class TenDrawPlayVM {

    @ApiModelProperty(value = "ID")
    @NotNull(message = "游戏ID不能为空")
    private Integer gameId;

}
