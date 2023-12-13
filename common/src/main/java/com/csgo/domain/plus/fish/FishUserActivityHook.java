package com.csgo.domain.plus.fish;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 用户钓鱼明细
 */
@Data
@TableName("fish_user_activity_hook")
public class FishUserActivityHook extends BaseEntity {
    //用户id
    @TableField(value = "user_id")
    private Integer userId;
    //场次类型(1:初级，2:中级，3:高级)
    @TableField(value = "session_type")
    private Integer sessionType;
    //活动id
    @TableField(value = "activity_id")
    private Integer activityId;
    //第几轮
    @TableField(value = "turn")
    private Integer turn;
    //挂机状态(0:未结束，1:已结束)
    @TableField(value = "hook_state")
    private Integer hookState;
    //挂机时间(秒)
    @TableField(value = "hook_time")
    private Integer hookTime;
    //开始时间
    @TableField(value = "begin_time")
    private Date beginTime;
    //结束时间
    @TableField(value = "end_time")
    private Date endTime;
    //实际结束时间
    @TableField(value = "finish_time")
    private Date finishTime;
}
