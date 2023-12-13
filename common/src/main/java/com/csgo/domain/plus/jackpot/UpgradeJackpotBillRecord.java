package com.csgo.domain.plus.jackpot;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
@TableName("upgrade_jackpot_bill_record")
public class UpgradeJackpotBillRecord extends BaseEntity {

    @TableField("balance")
    private BigDecimal balance;
    @TableField("amount")
    private BigDecimal amount;
    @TableField("phone")
    private String phone;
    @TableField("user_id")
    private int userId;
    @TableField("user_name")
    private String userName;
}
