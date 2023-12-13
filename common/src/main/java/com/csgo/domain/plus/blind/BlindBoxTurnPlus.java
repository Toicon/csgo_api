package com.csgo.domain.plus.blind;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
@TableName("blind_box_turn")
public class BlindBoxTurnPlus {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("user_id")
    private Integer userId;
    @TableField("room_num")
    private String roomNum;
    private Integer turn;
    @TableField("lottery_number")
    private String lotteryNumber;
    private Integer integral;
    private Integer seat;
    @TableField("blind_box_id")
    private Integer blindBoxId;
    @TableField("product_id")
    private Integer productId;
    private BigDecimal price;
    @TableField("product_name")
    private String productName;
    @TableField("img_url")
    private String imgUrl;
    @TableField("add_time")
    private Date addTime;
    @TableField("update_time")
    private Date updateTime;
}
