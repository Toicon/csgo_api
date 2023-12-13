package com.csgo.web.response.fish;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 钓鱼玩法-活动挂机
 */
@Data
public class FishUserHookResponse {
    /**
     * 倒计时(秒)
     */
    @ApiModelProperty(notes = "倒计时(秒)")
    private Integer countDown;
    /**
     * 第几轮次
     */
    @ApiModelProperty(notes = "第几轮次")
    private Integer turn;

    /**
     * 剩余轮次
     */
    @ApiModelProperty(notes = "剩余轮次")
    private Integer remainCount;

}
