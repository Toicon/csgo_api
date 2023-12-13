package com.csgo.domain.user;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "佣金记录表", description = "佣金记录")
@Entity
@Table(name = "user_commission_log")
public class UserCommissionLog {
    @ApiModelProperty(value = "主键ID", required = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty(value = "充值用户ID", required = true)
    @Column(name = "user_id")
    private Integer userId;

    @ApiModelProperty(value = "1 一级分佣 2二级分佣", required = true)
    private Integer grade;

    @ApiModelProperty(value = "充值金额", required = true)
    private BigDecimal amount;

    @ApiModelProperty(value = "佣金比例", required = true)
    private Double proportion;

    @ApiModelProperty(value = "分佣用户", required = true)
    @Column(name = "commission_user_id")
    private Integer commissionUserId;

    @ApiModelProperty(value = "所得佣金", required = true)
    @Column(name = "commission_amount")
    private BigDecimal commissionAmount;

    @ApiModelProperty(value = "是否结算 1未领取 2已领取  3不可领取", required = true)
    private Integer status;

    @ApiModelProperty(value = "订单编号", required = true)
    @Column(name = "order_num")
    private String orderNum;

    @ApiModelProperty("新增时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "settlement_time")
    private Date settlementTime;

    @ApiModelProperty("新增时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "add_time")
    private Date addTime;

    @ApiModelProperty("新增时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private Date updateTime;


    @Transient
    private Integer count;

    @Transient
    private String name;

    @Transient
    private String commissionName;

}
