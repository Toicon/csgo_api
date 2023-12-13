package com.csgo.domain.plus.blind;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("blind_box_type")
public class BlindBoxTypePlus {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    @TableField("sort_id")
    private Integer sortId;
    @TableField("add_time")
    private Date addTime;
    @TableField("update_time")
    private Date updateTime;
}