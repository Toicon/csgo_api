package com.csgo.domain.plus.anchor;

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
@TableName("box_anchor_jackpot")
public class BoxAnchorJackpot extends BaseEntity {

    @TableField(value = "balance")
    private BigDecimal balance;

}
