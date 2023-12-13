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
@TableName("fish_anchor_spare_jackpot")
public class FishAnchorSpareJackpot extends BaseEntity {

    @TableField(value = "balance")
    private BigDecimal balance;

}
