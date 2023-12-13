package com.csgo.domain;

import com.csgo.domain.user.UserRoomImgDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "盲盒房间表", description = "盲盒房间")
@Entity
public class BlindBoxRoom implements Serializable {
    @ApiModelProperty(value = "主键ID", required = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty(value = "房间编号", required = true)
    private String roomNum;

    @ApiModelProperty(value = "本场随机值", required = true)
    private String randomNum;

    @ApiModelProperty(value = "座位号（盒子数乘以参加的用户数）", required = true)
    private Integer seat;

    @ApiModelProperty(value = "价格", required = true)
    private BigDecimal price;

    @ApiModelProperty(value = "2/3 双人模式/三人模式", required = true)
    private Integer personPattern;

    @ApiModelProperty(value = "用户ID", required = true)
    private Integer userId;

    @ApiModelProperty(value = "盲盒数量", required = true)
    private Integer blindBoxNum;

    @ApiModelProperty(value = "当前房间人数", required = true)
    private Integer currentNum;

    @ApiModelProperty(value = "当前房间执行的轮数", required = true)
    private Integer currentTurn;

    @ApiModelProperty(value = "0 等待中 1 后端进行中 2 已结束 -1 前端进行中", required = true)
    private Integer status;

    @ApiModelProperty(value = "房间总积分", required = true)
    private Integer integral;

    @ApiModelProperty(value = "胜利者的用户id逗号分隔", required = true)
    private String victoryUserIds;

    @ApiModelProperty(value = "房间的用户id逗号分隔", required = true)
    private String roomUserIds;

    @ApiModelProperty(value = "输赢状态（1 共赢  2 单人赢  3 多人赢）", required = true)
    private Integer victoryStatus;

    @ApiModelProperty("新增时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "add_time")
    private Date addTime;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private Date updateTime;

    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "start_time")
    private Date startTime;


    @Transient
    private String img;

    @Transient
    private String name;

    @Transient
    private Integer roomCount;

    @Transient
    private BigDecimal totalPrice;

    @Transient
    private List<Integer> boxIdList;


    @Transient
    private List<String> boxImgList;

    @Transient
    private List<UserRoomImgDTO> userImgList;
}