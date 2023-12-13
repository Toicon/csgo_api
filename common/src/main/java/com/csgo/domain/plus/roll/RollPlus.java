package com.csgo.domain.plus.roll;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@TableName("roll")
public class RollPlus {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("rollname")
    private String rollName;
    @TableField("roll_type")
    private RollType rollType;
    @TableField("sort_id")
    private int sortId;
    private String type;
    @TableField("drawdate")
    private Date drawDate;
    @TableField("start_time")
    private Date startTime;
    @TableField("end_time")
    private Date endTime;
    private String password;
    @TableField("userlimit")
    private BigDecimal userLimit;
    @TableField("anchorlink")
    private String anchorLink;
    private String status;
    private Date ut;
    private Date ct;
    private Integer num;
    @TableField("anchoruserid")
    private Integer anchorUserId;
    @TableField("rollnumber")
    private String rollNumber;
    @TableField("rolldesc")
    private String rollDesc;
    @TableField("anchordesc")
    private String anchorDesc;
    @TableField("room_switch")
    private Boolean roomSwitch;
    private String img;

    @TableField("min_grade")
    private Integer minGrade;
    @TableField("max_grade")
    private Integer maxGrade;
}