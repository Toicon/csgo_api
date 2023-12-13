package com.csgo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "房间盲盒表", description = "房间盲盒")
@Entity
@Table(name = "room_box")
public class RoomBox implements Serializable {
    @ApiModelProperty(value = "主键ID", required = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty(value = "房间编号", required = true)
    @Column(name = "room_num")
    private String roomNum;

    @ApiModelProperty(value = "盲盒价格", required = true)
    private BigDecimal price;

    @ApiModelProperty(value = "盲盒名称", required = true)
    private String name;

    @ApiModelProperty(value = "盲盒图片", required = true)
    private String img;

    @ApiModelProperty(value = "等级", required = true)
    private Integer grade;

    @ApiModelProperty(value = "盲盒ID", required = true)
    @Column(name = "blind_box_id")
    private Integer blindBoxId;

    @ApiModelProperty("新增时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "add_time")
    private Date addTime;

}