package com.csgo.domain.plus.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("user_steam_update_record")
public class UserSteamUpdateRecord extends BaseEntity {
    @TableField("user_id")
    private Integer userId;
    @TableField("before_steam")
    private String beforeSteam;
    @TableField("after_steam")
    private String afterSteam;
    @TableField("steam_update_type")
    private SteamUpdateType steamUpdateType;
}
