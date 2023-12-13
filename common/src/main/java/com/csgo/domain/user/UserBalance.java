package com.csgo.domain.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "用户余额记录", description = "用户余额记录")
@Entity
public class UserBalance {
    @ApiModelProperty(value = "主键ID", required = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty(value = "记录编号", required = true)
    private String balanceNumber;

    @ApiModelProperty(value = "用户id", required = true)
    private Integer userId;

    @ApiModelProperty(value = "类型 1收入 2支出 ", required = true)
    private Integer type;

    @ApiModelProperty(value = "备注", required = true)
    private String remark;

    @ApiModelProperty(value = "金额", required = true)
    private BigDecimal amount;

    @ApiModelProperty(value = "银币余额", required = true)
    private BigDecimal diamondAmount;

    @ApiModelProperty(value = "变动后的金额", required = true)
    private BigDecimal currentAmount;

    @ApiModelProperty(value = "变动后的金额", required = true)
    private BigDecimal currentDiamondAmount;

    @ApiModelProperty("新增时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "add_time")
    private Date addTime;


    @Transient
    private String name;

}
