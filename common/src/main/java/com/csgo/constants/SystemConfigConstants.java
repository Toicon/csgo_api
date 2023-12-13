package com.csgo.constants;

/**
 * Created by Admin on 2021/5/4
 */
public final class SystemConfigConstants {

    //内部员工充值限制
    public static final String RECHARGE_LIMIT = "SYSTEM:RECHARGE_LIMIT";
    //用户充值限额，显示盲盒
    public static final String BLIND_BOX_RECHARGE_LIMIT = "SYSTEM:BLIND_BOX_RECHARGE_LIMIT";
    //提取道具总价值超过（1-100）%，走审批
    public static final String WITHDRAW_EXCEED = "SYSTEM:WITHDRAW_EXCEED";
    //提取最低金额限制
    public static final String WITHDRAW_LIMIT = "SYSTEM:WITHDRAW_LIMIT";
    //取回功能禁用
    public static final String WITHDRAW_DISABLED = "SYSTEM:WITHDRAW_DISABLED";
    //内部账号默认幸运值
    public static final String DEFAULT_INNER_USER_LUCKY = "SYSTEM:DEFAULT_INNER_USER_LUCKY";
    //会员等级有效期
    public static final String MEMBERSHIP_GROWTH_ACTIVE = "MEMBERSHIP:GROWTH_ACTIVE";
    //会员首充赠送比例
    public static final String MEMBERSHIP_FIRST_BOUND = "MEMBERSHIP:FIRST_BOUND";
    //测试奖池开箱开关
    public static final String ANCHOR_OPEN = "SYSTEM:ANCHOR:OPEN";
    //开箱均价涨幅
    public static final String SYSTEM_CUSTOMER_PRICE = "SYSTEM:CUSTOMER:PRICE";
    //续充折扣起始
    public static final String SYSTEM_DISCOUNT_MIN = "SYSTEM:DISCOUNT:MIN";
    //续充折扣截止
    public static final String SYSTEM_DISCOUNT_MAX = "SYSTEM:DISCOUNT:MAX";
    //续充倒计时
    public static final String SYSTEM_SECOND_COUNTDOWN = "SYSTEM:SECOND:COUNTDOWN";
    //续充触发概率
    public static final String SYSTEM_SECOND_PROBABILITY = "SYSTEM:SECOND:PROBABILITY";
}
