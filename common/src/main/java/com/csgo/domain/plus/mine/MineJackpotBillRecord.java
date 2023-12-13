package com.csgo.domain.plus.mine;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 扫雷奖池变化表
 *
 * @author admin
 */
@Data
@TableName("mine_jackpot_bill_record")
public class MineJackpotBillRecord extends BaseEntity {

    @TableField("balance")
    private BigDecimal balance;
    @TableField("amount")
    private BigDecimal amount;
    @TableField("name")
    private String name;
    @TableField("user_id")
    private int userId;
    @TableField("user_name")
    private String userName;
}
