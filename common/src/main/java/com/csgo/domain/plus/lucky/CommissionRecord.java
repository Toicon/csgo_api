package com.csgo.domain.plus.lucky;

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
@TableName("commission_record")
public class CommissionRecord {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    @TableField("balance_number")
    private String balanceNumber;
    @TableField("user_id")
    private int userId;
    private BigDecimal price;
    private Date ct;
}
