package com.csgo.web.request.fish;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 钓鱼-一键起竿
 */
@Data
public class FishTouchGiveUpRequest {
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
}
