package com.csgo.domain.plus.christmas;

import org.apache.ibatis.type.EnumTypeHandler;

/**
 * @author admin
 */
public enum RecordStatus {
    NO_STANDARD("未达标"),
    STANDARD("达标"),
    RECEIVE("已领取")
    ;
    private final String description;

    RecordStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static class TypeHandler extends EnumTypeHandler<RecordStatus> {
        public TypeHandler() {
            super(RecordStatus.class);
        }
    }
}
