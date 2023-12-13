package com.csgo.domain.plus.user;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("user_balance")
public class UserBalancePlus {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("balance_number")
    private String balanceNumber;
    @TableField("user_id")
    private Integer userId;
    private Integer type;
    private String remark;
    private BigDecimal amount;
    private BigDecimal diamondAmount;
    @TableField("current_amount")
    private BigDecimal currentAmount;
    @TableField("current_diamond_amount")
    private BigDecimal currentDiamondAmount;
    @TableField("add_time")
    private Date addTime;
}
