package com.csgo.domain.plus.withdraw;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.enums.WithdrawPropStatus;
import com.csgo.domain.plus.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
@TableName("withdraw_pop")
public class WithdrawProp extends BaseEntity {
    @TableField("user_id")
    private Integer userId;
    @TableField("drew_date")
    private Date drewDate;
    @TableField("status")
    private WithdrawPropStatus status;
}
