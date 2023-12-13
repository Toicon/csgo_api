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
@ApiModel(value = "购买盲盒记录表", description = "购买盲盒记录")
@Entity
public class UserBuyBoxLog {
    @ApiModelProperty(value = "主键ID", required = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty(value = "用户ID", required = true)
    private Integer userId;

    @ApiModelProperty(value = "房间编号 逗号分隔", required = true)
    private String roomNums;

    @ApiModelProperty(value = "房间数量", required = true)
    private Integer roomCount;

    @ApiModelProperty(value = "价格", required = true)
    private BigDecimal price;

    @ApiModelProperty("新增时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "add_time")
    private Date addTime;

    @Transient
    private String img;

    @Transient
    private String name;

}