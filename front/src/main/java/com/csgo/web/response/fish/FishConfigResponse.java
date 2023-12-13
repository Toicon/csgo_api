package com.csgo.web.response.fish;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 钓鱼玩法-场次配置信息
 */
@Data
public class FishConfigResponse {
    //礼包id
    @ApiModelProperty(notes = "礼包id")
    private Integer giftId;
    //场次类型(1:初级，2:中级，3:高级)
    @ApiModelProperty(notes = "场次类型(1:初级，2:中级，3:高级)")
    private Integer sessionType;
    //鱼竿类型(1蓝色,2红色,3金色)
    @ApiModelProperty(notes = "鱼竿类型(1蓝色,2红色,3金色)")
    private Integer fishRodType;
    //挂机状态
    @ApiModelProperty(notes = "挂机状态(0：未挂机，1：已挂机)")
    private Integer hookState;
}
