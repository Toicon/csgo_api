package com.csgo.domain.plus.jackpot;

import org.apache.ibatis.type.EnumTypeHandler;

/**
 * @author admin
 */
public enum JackpotType {
    BOX("开箱"),
    ANCHORBOX("主播开箱"),
    BATTLE("盲盒对战"),
    UPGRADE("饰品升级"),
    MINE("扫雷玩法"),
    TEN_DRAW("十连"),
    TEN_DRAW_ANCHOR("十连主播"),
    NOV_BOMB("模拟拆弹"),
    NOV_BOMB_ANCHOR("模拟拆弹主播"),
    ;
    private final String description;

    JackpotType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static class TypeHandler extends EnumTypeHandler<JackpotType> {
        public TypeHandler() {
            super(JackpotType.class);
        }
    }
}
