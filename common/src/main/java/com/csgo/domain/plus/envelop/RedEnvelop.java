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
@TableName(value = "red_envelop")
public class RedEnvelop {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

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

    @TableField("auto")
    private boolean auto;

    @TableField("status")
    private RedEnvelopStatus status;

    @TableField("time_interval")
    private int timeInterval;

    @TableField("show_num")
    private boolean showNum = true;

    @TableField("grade")
    private Integer grade;

    @TableField("sort_id")
    private Integer sortId;

    @TableField("create_date")
    private Date createDate;

    @TableField("update_date")
    private Date updateDate;

    @TableField("create_by")
    private String createBy;

    @TableField("update_by")
    private String updateBy;

}