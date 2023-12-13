package com.csgo.domain.plus.recharge;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.plus.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@TableName("recharge_channel")
@Getter
@Setter
public class RechargeChannel extends BaseEntity {

    @TableField("type")
    private RechargeChannelType type;
    @TableField("method")
    private RechargeMethod method;
    @TableField("sort_id")
    private int sortId;
    @TableField("hidden")
    private boolean hidden;
    @TableField("app")
    private boolean app;
}
