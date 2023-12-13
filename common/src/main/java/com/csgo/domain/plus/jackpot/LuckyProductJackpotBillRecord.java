package com.csgo.domain.plus.jackpot;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("lucky_product_jackpot_bill_record")
public class LuckyProductJackpotBillRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("balance_number")
    private String balanceNumber;
    private BigDecimal income;
    private String cb;
    private Date ct;
}
