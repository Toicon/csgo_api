package com.csgo.domain.plus.envelop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.csgo.domain.enums.RedEnvelopStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class RedEnvelopItemDTO {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableId(value = "envelop_id")
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

    private boolean showNum;

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

    @TableField("auto")
    private boolean auto;
}