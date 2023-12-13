package com.csgo.domain.plus.envelop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.enums.RedEnvelopStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@TableName(value = "red_envelop_item")
public class RedEnvelopItem {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("envelop_id")
    private Integer envelopId;

    @TableField("name")
    private String name;

    @TableField("min_amount")
    private BigDecimal minAmount;

    @TableField("max_amount")
    private BigDecimal maxAmount;

    @TableField("limit_amount")
    private BigDecimal limitAmount;

    @TableField("num")
    private Integer num;

    @TableField("token")
    private String token;

    @TableField("effective_start_time")
    private Date effectiveStartTime;

    @TableField("effective_end_time")
    private Date effectiveEndTime;

    @TableField("status")
    private RedEnvelopStatus status;

    @TableField("create_date")
    private Date createDate;

}