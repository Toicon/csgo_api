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
@TableName("battle_spare_jackpot_bill_record")
public class BattleSpareJackpotBillRecord extends BaseEntity {
    @TableField("amount")
    private BigDecimal amount;
}
