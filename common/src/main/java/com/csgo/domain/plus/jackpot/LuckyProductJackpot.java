package com.csgo.domain.plus.jackpot;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
@TableName("lucky_product_jackpot")
public class LuckyProductJackpot {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private BigDecimal balance;
    private Date ut;
    private String ub;
}
