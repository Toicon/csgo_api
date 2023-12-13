package com.csgo.modular.tendraw.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Data
@TableName("ten_draw_jackpot_bill_record")
public class TenDrawJackpotBillRecordDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "type")
    private Integer type;

    @TableField("before_balance")
    private BigDecimal beforeBalance;

    @TableField("new_balance")
    private BigDecimal newBalance;

    @TableField("change_balance")
    private BigDecimal changeBalance;

    @TableField("jacket_balance")
    private BigDecimal jacketBalance;

    @TableField("spare_balance")
    private BigDecimal spareBalance;

    @TableField("phone")
    private String phone;

    @TableField("user_id")
    private Integer userId;

    @TableField("user_name")
    private String userName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private Date createDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_date", fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    @TableField(value = "create_by", fill = FieldFill.INSERT, jdbcType = JdbcType.VARCHAR)
    private String createBy;

    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE, jdbcType = JdbcType.VARCHAR)
    private String updateBy;

}
