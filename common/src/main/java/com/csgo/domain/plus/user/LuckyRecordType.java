package com.csgo.domain.plus.user;

/**
 * @author admin
 */
public enum LuckyRecordType {

    ACCESSORY("饰品升级"), HOME("首页开箱");

    private final String dis;

    LuckyRecordType(String dis) {
        this.dis = dis;
    }

    public String getDis() {
        return dis;
    }
}
