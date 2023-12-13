package com.csgo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "盲盒房间轮次表", description = "盲盒房间轮次")
@Entity
public class BlindBoxTurn {
    @ApiModelProperty(value = "主键ID", required = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty(value = "用户ID", required = true)
    private Integer userId;

    @ApiModelProperty(value = "房间编号", required = true)
    private String roomNum;

    @ApiModelProperty(value = "轮次第几轮", required = true)
    private Integer turn;

    @ApiModelProperty(value = "开奖编号", required = true)
    private String lotteryNumber;

    @ApiModelProperty(value = "积分", required = true)
    private Integer integral;

    @ApiModelProperty(value = "座位", required = true)
    private Integer seat;

    @ApiModelProperty(value = "商品Id", required = true)
    private Integer productId;

    @ApiModelProperty(value = "获得金额", required = true)
    private BigDecimal price;

    @ApiModelProperty(value = "奖品名称", required = true)
    private String productName;

    @ApiModelProperty(value = "奖品图片", required = true)
    private String imgUrl;


    @ApiModelProperty("新增时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "add_time")
    private Date addTime;

    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private Date updateTime;

}