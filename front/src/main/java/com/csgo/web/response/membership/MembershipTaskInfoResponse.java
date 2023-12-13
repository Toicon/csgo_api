package com.csgo.web.response.membership;

import com.csgo.domain.plus.membership.MembershipTaskRule;
import com.csgo.domain.plus.membership.MembershipTaskStatus;
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
@ApiModel(value = "获取积分列表结果", description = "获取积分列表结果")
public class MembershipTaskInfoResponse {
    @ApiModelProperty(notes = "领取积分时用", example = "1")
    private int id;
    @ApiModelProperty(notes = "任务类型", example = "DAY_SIGN(每日签到),DAY_BOX(每日开箱5次),BOX_PRICE_100_OVER(宝箱内开出价值大于100V币的物品),UPGRADE_PRICE_100_OVER (饰品内升级价值大于100V币的物品),FIGHT_WIN(获得1次大乱斗的胜利),INVITE(邀请一个用户并充值（可叠加）),WITHDRAW(成功进行饰品提取),DAY_UPGRADE(每日饰品升级5次)")
    private MembershipTaskRule ruleType;
    @ApiModelProperty(notes = "任务状态", example = "NO_STANDARD(未达标),STANDARD(达标),RECEIVE(已领取)")
    private MembershipTaskStatus recordStatus;
    @ApiModelProperty(notes = "总数", example = "5")
    private int totalCount;
    @ApiModelProperty(notes = "当前数量", example = "5")
    private int currentCount;
    @ApiModelProperty(notes = "奖励", example = "1")
    private BigDecimal reward;
}
