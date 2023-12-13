package com.csgo.domain.plus.user;

/**
 * @author admin
 */
public enum SteamUpdateType {

    ADMIN("管理员"), USER("用户");

    private final String dis;

    SteamUpdateType(String dis) {
        this.dis = dis;
    }

    public String getDis() {
        return dis;
    }
}
