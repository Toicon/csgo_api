package com.csgo.domain.plus.christmas;

import org.apache.ibatis.type.EnumTypeHandler;

/**
 * @author admin
 */
public enum BillType {
    RECEIVE("完成任务领取"),
    LUCK_DRAW("抽奖"),
    ;
    private final String description;

    BillType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static class TypeHandler extends EnumTypeHandler<BillType> {
        public TypeHandler() {
            super(BillType.class);
        }
    }
}
