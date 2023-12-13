package com.csgo.domain.plus.mine;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 扫雷备用库存
 *
 * @author admin
 */
@Data
@TableName("mine_spare_jackpot")
public class MineSpareJackpot extends BaseEntity {

    @TableField(value = "balance")
    private BigDecimal balance;

}
