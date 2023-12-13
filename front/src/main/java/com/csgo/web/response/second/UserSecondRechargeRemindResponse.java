package com.csgo.web.response.second;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 获取用户二次充值提醒
 */
@Data
public class UserSecondRechargeRemindResponse {

    /**
     * 倒计时(秒)
     */
    @ApiModelProperty(notes = "倒计时(秒)")
    private Integer countDown;

    /**
     * 折扣领取状态(0:不能领取，1：可以领取)
     */
    @ApiModelProperty(value = "折扣领取状态(0:不能领取，1：可以领取)")
    private boolean receiveState;

    /**
     * 额外赠送折扣
     */
    @ApiModelProperty(value = "额外赠送折扣")
    private BigDecimal discount;


}
