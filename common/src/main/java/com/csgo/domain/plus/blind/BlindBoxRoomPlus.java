package com.csgo.domain.plus.blind;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("blind_box_room")
public class BlindBoxRoomPlus {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("user_id")
    private Integer userId;
    @TableField("room_user_ids")
    private String roomUserIds;
    @TableField("room_num")
    private String roomNum;
    @TableField("random_num")
    private String randomNum;
    private Integer seat;
    private Date startTime;
    @TableField("person_pattern")
    private Integer personPattern;
    @TableField("blind_box_num")
    private Integer blindBoxNum;
    private BigDecimal price;
    private Integer status;
    private Integer integral;
    @TableField("current_num")
    private Integer currentNum;
    @TableField("victory_user_ids")
    private String victoryUserIds;
    @TableField("victory_status")
    private Integer victoryStatus;
    @TableField("add_time")
    private Date addTime;
    @TableField("update_time")
    private Date updateTime;
    @TableField("current_turn")
    private Integer currentTurn;
    @TableField("need_pwd")
    private boolean needPwd;
    @TableField("room_password")
    private String roomPassword;
}
