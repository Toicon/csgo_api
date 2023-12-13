package com.csgo.domain.plus.membership;

import org.apache.ibatis.type.EnumTypeHandler;

/**
 * @author admin
 */
public enum MembershipTaskStatus {
    NO_STANDARD("未达标"),
    STANDARD("达标"),
    RECEIVE("已领取");
    private final String description;

    MembershipTaskStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static class TypeHandler extends EnumTypeHandler<MembershipTaskStatus> {
        public TypeHandler() {
            super(MembershipTaskStatus.class);
        }
    }
}
