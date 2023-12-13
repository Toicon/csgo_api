package com.csgo.domain.plus.mine;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import lombok.Data;

/**
 * 扫雷活动用户每关奖品
 *
 * @author admin
 */
@Data
@TableName("mine_user_activity_prize")
public class MineUserActivityPrize extends BaseEntity {
    //用户id
    @TableField(value = "user_id")
    private Integer userId;
    //活动id
    @TableField(value = "activity_id")
    private Integer activityId;
    //第几关
    @TableField(value = "level")
    private Integer level;
    //奖品商品id
    @TableField(value = "gift_product_id")
    private Integer giftProductId;
}
