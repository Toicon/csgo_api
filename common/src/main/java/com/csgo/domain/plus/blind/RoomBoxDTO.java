package com.csgo.domain.plus.blind;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class RoomBoxDTO {
    private Integer id;
    @TableField("room_num")
    private String roomNum;
    private BigDecimal price;
    private String name;
    private String img;
    private Integer grade;
    @TableField("blind_box_id")
    private Integer blindBoxId;
    @TableField("add_time")
    private Date addTime;
    @TableField("box_img")
    private String boxImg;
}