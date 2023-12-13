package com.csgo.domain.plus.user;

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
@TableName("recharge_record")
@Getter
@Setter
public class RechargeRecord {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    @TableField("user_id")
    private int userId;
    @TableField("price")
    private BigDecimal price;
    @TableField("balance")
    private BigDecimal balance;
    @TableField("order_num")
    private String orderNum;
    @TableField("remark")
    private String remark;
    private String cb;
    private Date ct;
}
