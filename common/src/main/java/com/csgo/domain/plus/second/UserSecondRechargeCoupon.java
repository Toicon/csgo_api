package com.csgo.domain.plus.second;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户二次充值优惠券
 */
@Data
@TableName("user_second_recharge_coupon")
public class UserSecondRechargeCoupon extends BaseEntity {
    //用户id
    @TableField(value = "user_id")
    private Integer userId;
    //折扣比例
    @TableField(value = "discount_ratio")
    private BigDecimal discountRatio;
    //使用状态(0:未使用,1:已使用)
    @TableField(value = "use_state")
    private Integer useState;
    //优惠券失效时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("failure_time")
    private Date failureTime;
    //充值订单号
    @TableField(value = "order_num")
    private String orderNum;
}
