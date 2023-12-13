package com.csgo.domain.plus.second;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户每日开箱累计
 */
@Data
@TableName("user_second_recharge_day")
public class UserSecondRechargeDay extends BaseEntity {
    //用户id
    @TableField(value = "user_id")
    private Integer userId;
    //累计开箱次数
    @TableField(value = "open_count")
    private Long openCount;
    //累计消耗价值
    @TableField(value = "consume_amount")
    private BigDecimal consumeAmount;
    //创建日期(不包含时分秒)
    @TableField("found_date")
    private Date foundDate;
}
