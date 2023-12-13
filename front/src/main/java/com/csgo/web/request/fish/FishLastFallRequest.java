package com.csgo.web.request.fish;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 钓鱼-最近掉落
 */
@Data
public class FishLastFallRequest {
    /**
     * 礼包id
     */
    @ApiModelProperty(notes = "礼包id")
    @NotNull(message = "礼包id不能为空")
    private Integer giftId;
}
