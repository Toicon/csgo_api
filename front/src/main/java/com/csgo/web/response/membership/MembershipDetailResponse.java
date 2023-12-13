package com.csgo.web.response.membership;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Admin on 2021/12/14
 */
@Setter
@Getter
@ApiModel(value = "获取VIP等级配置信息响应结果", description = "获取VIP等级配置信息响应结果")
public class MembershipDetailResponse {
    /**
     * 等级
     */
    @ApiModelProperty(notes = "VIP等级", example = "1")
    private Integer level;
    /**
     * 充值金额
     */
    @ApiModelProperty(notes = "所需积分", example = "100")
    private BigDecimal levelLimit;
    /**
     * 赠送充值比例
     */
    @ApiModelProperty(notes = "赠送充值比例", example = "10")
    private BigDecimal giftRate;
    /**
     * 头像图标
     */
    @ApiModelProperty(notes = "头像图标", example = "头像图标")
    private String img;
    /**
     * 宝箱图标
     */
    @ApiModelProperty(notes = "宝箱图标", example = "宝箱图标")
    private String boxImg;
    /**
     * roll房名称
     */
    @ApiModelProperty(notes = "roll房名称", example = "roll房名称")
    private String rollName;
    /**
     * 红包奖励
     */
    @ApiModelProperty(notes = "红包奖励金额", example = "5")
    private BigDecimal redAmount;
    @ApiModelProperty(notes = "红包状态", example = "UNRECEIVED(\"不可领取\"), ACCEPTABLE(\"可领取\"), RECEIPTED(\"已领取\")")
    private MembershipRedEnvelopStatus status;
}
