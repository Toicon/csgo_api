package com.csgo.web.request.fish;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 钓鱼-当前活动信息
 */
@Data
public class FishActivityInfoRequest {
    /**
     * 礼包id
     */
    @ApiModelProperty(notes = "礼包id")
    @NotNull(message = "礼包id不能为空")
    private Integer giftId;
}
