package com.csgo.domain.plus.fish;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Getter
@Setter
@TableName("fish_user_jackpot_bill_record")
public class FishUserJackpotBillRecord extends BaseEntity {

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
