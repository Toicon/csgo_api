package com.csgo.domain.plus.membership;

import com.baomidou.mybatisplus.core.enums.IEnum;

import java.io.Serializable;

/**
 * @author admin
 */
public enum MembershipLevelRecordType implements IEnum {

    RECHARGE("充值"), SIGN_IN("签到"), UNPACK("每日开箱5次"), BOX_PRICE("宝箱价值大于100V币"), ACCESSORY_PRICE("饰品升级价值大于100V币"),
    BLIND_FIGHT("大乱斗胜利"), INVITATION("邀请用户并充值"), WITHDRAW("成功提取"), ACCESSORY("每日饰品升级5次");

    private String description;

    MembershipLevelRecordType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public Serializable getValue() {
        return this.name();
    }
}
