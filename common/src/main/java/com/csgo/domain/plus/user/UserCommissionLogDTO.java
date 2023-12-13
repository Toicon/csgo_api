package com.csgo.domain.plus.user;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class UserCommissionLogDTO {
    private BigDecimal amount;
    private BigDecimal count;
    private BigDecimal proportion;
    @TableField("commission_amount")
    private BigDecimal commissionAmount;
    private Integer status;
    @TableField("settlement_time")
    private Date settlementTime;
}