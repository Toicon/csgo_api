package com.csgo.domain.plus.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@TableName("user_commission_log")
public class UserCommissionLogPlus {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("user_id")
    private Integer userId;
    private Integer grade;
    private BigDecimal amount;
    private BigDecimal proportion;
    @TableField("commission_user_id")
    private Integer commissionUserId;
    @TableField("commission_amount")
    private BigDecimal commissionAmount;
    private Integer status;
    @TableField("order_num")
    private String orderNum;
    @TableField("settlement_time")
    private Date settlementTime;
    @TableField("add_time")
    private Date addTime;
    @TableField("update_time")
    private Date updateTime;
}