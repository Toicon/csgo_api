package com.csgo.domain.plus.mine;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 扫雷奖池
 *
 * @author admin
 */
@Data
@TableName("mine_jackpot")
public class MineJackpot extends BaseEntity {
    @TableField(value = "balance")
    private BigDecimal balance;
}
