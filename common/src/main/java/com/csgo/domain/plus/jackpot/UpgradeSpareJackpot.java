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
@TableName("upgrade_spare_jackpot")
public class UpgradeSpareJackpot extends BaseEntity {

    @TableField(value = "balance")
    private BigDecimal balance;

}
