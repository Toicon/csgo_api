package com.csgo.web.response.membership;

/**
 * Created by admin on 2021/12/15
 */
public enum MembershipRedEnvelopStatus {
    UNRECEIVED("不可领取"), ACCEPTABLE("可领取"), RECEIPTED("已领取");
    private String description;

    MembershipRedEnvelopStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
