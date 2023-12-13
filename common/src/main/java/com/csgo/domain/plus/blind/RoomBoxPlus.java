package com.csgo.domain.plus.blind;

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
@TableName("room_box")
public class RoomBoxPlus {
    @TableId(value = "id", type = IdType.AUTO)
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

}