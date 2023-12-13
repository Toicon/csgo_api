package com.csgo.web.request.fish;

import com.echo.framework.platform.web.request.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 钓鱼-我的网箱
 */
@Data
public class FishMyUserPrizeRequest extends PageRequest {
    /**
     * 礼包id
     */
    @ApiModelProperty(notes = "礼包id")
    @NotNull(message = "礼包id不能为空")
    private Integer giftId;
}
