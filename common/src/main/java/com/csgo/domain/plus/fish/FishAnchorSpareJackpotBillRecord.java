package com.csgo.domain.plus.fish;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@TableName("fish_anchor_spare_jackpot_bill_record")
public class FishAnchorSpareJackpotBillRecord extends BaseEntity {
    @TableField("amount")
    private BigDecimal amount;
}
