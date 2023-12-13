package com.csgo.domain.plus.mine;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 扫雷用户活动
 *
 * @author admin
 */
@Data
@TableName("mine_user_activity")
public class MineUserActivity extends BaseEntity {
    //用户id
    @TableField(value = "user_id")
    private Integer userId;
    //活动结束状态(0:未结束，1:已结束)
    @TableField(value = "finish_state")
    private Integer finishState;
    //支付状态(0:未支付，1:已支付)
    @TableField(value = "pay_state")
    private Integer payState;
    //支付金额
    @TableField(value = "pay_price")
    private BigDecimal payPrice;
}
