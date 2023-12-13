package com.csgo.domain.plus.withdraw;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Getter
@Setter
public class WithdrawPropPriceDTO {

    @TableField("user_id")
    private Integer userId;
    private BigDecimal price;
}
