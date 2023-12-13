package com.csgo.domain.plus.anchor;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import lombok.Data;

/**
 * 测试奖池开箱关闭白名单
 */
@Data
@TableName("user_anchor_white")
public class UserAnchorWhite extends BaseEntity {
    //用户id
    @TableField(value = "user_id")
    private Integer userId;
}
