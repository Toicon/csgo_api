package com.csgo.web.request.fish;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 钓鱼-定时起竿
 */
@Data
public class FishTimeGiveUpRequest {
    /**
     * 礼包id
     */
    @ApiModelProperty(notes = "礼包id")
    @NotNull(message = "礼包id不能为空")
    private Integer giftId;
    /**
     * 第几轮次
     */
    @ApiModelProperty(notes = "第几轮次")
    @NotNull(message = "第几轮次不能为空")
    private Integer turn;
}
