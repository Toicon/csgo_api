package com.csgo.web.request.fish;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 钓鱼-配置信息
 */
@Data
public class FishActivityConfigRequest {
    /**
     * 场次类型(1:初级，2:中级，3:高级)
     */
    @NotNull(message = "场次类型不能为空")
    @ApiModelProperty(notes = "场次类型(1:初级，2:中级，3:高级)")
    private Integer sessionType;
}
