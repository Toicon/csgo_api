package com.csgo.web.request.fish;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 钓鱼-活动挂机
 */
@Data
public class FishActivityHookRequest {
    /**
     * 礼包id
     */
    @ApiModelProperty(notes = "礼包id")
    @NotNull(message = "礼包id不能为空")
    private Integer giftId;
    /**
     * 鱼饵id
     */
    @ApiModelProperty(notes = "鱼饵id")
    private Integer baitId;
    /**
     * 极速模式(0:否，1：是)
     */
    @ApiModelProperty(notes = "极速模式(0:否，1：是)")
    @NotNull(message = "极速模式不能为空")
    private Integer fishMode;
    /**
     * 挂机次数
     */
    @ApiModelProperty(notes = "挂机次数")
    @NotNull(message = "挂机次数不能为空")
    private Integer turnCount;
}
