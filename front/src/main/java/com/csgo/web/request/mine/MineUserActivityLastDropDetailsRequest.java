package com.csgo.web.request.mine;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 最近掉落详情
 */
@Data
public class MineUserActivityLastDropDetailsRequest {

    /**
     * 最近掉落id
     */
    @ApiModelProperty(notes = "最近掉落id")
    @NotNull(message = "最近掉落id不能为空")
    private Integer id;

}
