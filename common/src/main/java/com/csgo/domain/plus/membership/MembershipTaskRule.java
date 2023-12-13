package com.csgo.domain.plus.membership;

import org.apache.ibatis.type.EnumTypeHandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author admin
 */
public enum MembershipTaskRule {
    DAY_SIGN("每日签到", 1, BigDecimal.ONE),
    DAY_BOX("每日开箱5次", 5, BigDecimal.ONE),
    BOX_PRICE_100_OVER("宝箱内开出价值大于100V币的物品", 1, BigDecimal.ONE),
    UPGRADE_PRICE_100_OVER("饰品内升级价值大于100V币的物品", 1, BigDecimal.ONE),
    FIGHT_WIN("获得1次大乱斗的胜利", 1, BigDecimal.ONE),
    INVITE("邀请一个用户并充值（可叠加）", 1, BigDecimal.valueOf(5)),
    WITHDRAW("成功进行饰品提取", 1, BigDecimal.valueOf(5)),
    DAY_UPGRADE("每日饰品升级5次", 5, BigDecimal.valueOf(5));
    private final String description;
    private final int ruleCount;
    private final BigDecimal reward;

    MembershipTaskRule(String description, int ruleCount, BigDecimal reward) {
        this.description = description;
        this.ruleCount = ruleCount;
        this.reward = reward;
    }

    public String getDescription() {
        return description;
    }


    public int getRuleCount() {
        return ruleCount;
    }

    public BigDecimal getReward() {
        return reward;
    }

    public static List<MembershipTaskRule> getTodayRules() {
        List<MembershipTaskRule> ruleTypes = new ArrayList<>();
        ruleTypes.add(MembershipTaskRule.DAY_SIGN);
        ruleTypes.add(MembershipTaskRule.DAY_BOX);
        ruleTypes.add(MembershipTaskRule.DAY_UPGRADE);
        ruleTypes.add(MembershipTaskRule.FIGHT_WIN);
        ruleTypes.add(MembershipTaskRule.WITHDRAW);
        ruleTypes.add(MembershipTaskRule.UPGRADE_PRICE_100_OVER);
        ruleTypes.add(MembershipTaskRule.BOX_PRICE_100_OVER);
        return ruleTypes;
    }

    public static class TypeHandler extends EnumTypeHandler<MembershipTaskRule> {
        public TypeHandler() {
            super(MembershipTaskRule.class);
        }
    }
}
