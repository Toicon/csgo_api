package com.csgo.web.response.fish;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 钓鱼玩法-当前活动信息
 */
@Data
public class FishUserInfoResponse {
    /**
     * 礼包id
     */
    @ApiModelProperty(notes = "礼包id")
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
    private Integer fishMode;

    /**
     * 剩余轮次
     */
    @ApiModelProperty(notes = "剩余轮次")
    private Integer remainCount;
    /**
     * 挂机状态(0:否，1：是)
     */
    @ApiModelProperty(notes = "挂机状态(0:否，1：是)")
    private Integer hookState;
    /**
     * 第几轮次
     */
    @ApiModelProperty(notes = "第几轮次")
    private Integer turn;
    /**
     * 倒计时(秒)
     */
    @ApiModelProperty(notes = "倒计时(秒)")
    private Integer countDown;
    /**
     * 支付金额
     */
    @ApiModelProperty(notes = "支付金额")
    private BigDecimal payPrice;
}
