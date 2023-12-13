package com.csgo.domain.plus.fish;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import lombok.Data;

/**
 * 钓鱼活动配置信息
 */
@Data
@TableName("fish_config")
public class FishConfig extends BaseEntity {
    //礼包id
    @TableField(value = "gift_id")
    private Integer giftId;
    //场次类型(1:初级，2:中级，3:高级)
    @TableField(value = "session_type")
    private Integer sessionType;
    //鱼竿类型(1蓝色,2红色,3金色)
    @TableField(value = "fish_rod_type")
    private Integer fishRodType;
}
