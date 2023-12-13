package com.csgo.domain.plus.box;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
@TableName("treasure_box_relate")
public class TreasureBoxRelate {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    @TableField("treasure_box_id")
    private int treasureBoxId;
    @TableField("gift_id")
    private int giftId;
    private String cb;
    private Date ct;
}
