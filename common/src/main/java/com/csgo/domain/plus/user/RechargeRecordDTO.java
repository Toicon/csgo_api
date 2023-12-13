package com.csgo.domain.plus.user;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class RechargeRecordDTO {

    @TableField("user_name")
    private String userName;
    private Integer flag;
    private Tag tag;
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
