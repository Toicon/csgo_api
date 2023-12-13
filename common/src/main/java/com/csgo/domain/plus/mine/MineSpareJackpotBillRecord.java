package com.csgo.domain.plus.mine;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 扫雷备用库存变化
 *
 * @author admin
 */
@Data
@TableName("mine_spare_jackpot_bill_record")
public class MineSpareJackpotBillRecord extends BaseEntity {
    @TableField("amount")
    private BigDecimal amount;
}
