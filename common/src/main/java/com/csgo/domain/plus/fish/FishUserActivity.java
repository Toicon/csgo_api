package com.csgo.domain.plus.fish;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 钓鱼用户活动
 */
@Data
@TableName("fish_user_activity")
public class FishUserActivity extends BaseEntity {
    //场次类型(1:初级，2:中级，3:高级)
    @TableField(value = "session_type")
    private Integer sessionType;
    //礼包id
    @TableField(value = "gift_id")
    private Integer giftId;
    //鱼饵id
    @TableField(value = "bait_id")
    private Integer baitId;
    //用户id
    @TableField(value = "user_id")
    private Integer userId;
    //是否主动起竿(0:否，1：是)
    @TableField(value = "active_state")
    private Integer activeState;
    //极速模式(0:否，1：是)
    @TableField(value = "fish_mode")
    private Integer fishMode;
    //活动结束状态(0:未结束，1:已结束)
    @TableField(value = "finish_state")
    private Integer finishState;
    //支付金额
    @TableField(value = "pay_price")
    private BigDecimal payPrice;
    //挂机时间(秒)
    @TableField(value = "hook_time")
    private Integer hookTime;
    //总轮数
    @TableField(value = "turn_count")
    private Integer turnCount;
    //开始时间
    @TableField(value = "begin_time")
    private Date beginTime;
    //结束时间
    @TableField(value = "end_time")
    private Date endTime;
    //失效时间
    @TableField(value = "failure_time")
    private Date failureTime;
    //失效备注
    @TableField(value = "failure_remark")
    private String failureRemark;
}
