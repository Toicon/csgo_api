package com.csgo.domain.plus.fish;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@TableName("fish_anchor_jackpot")
public class FishAnchorJackpot extends BaseEntity {

    @TableField(value = "balance")
    private BigDecimal balance;

}
