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
@TableName("blind_box")
public class BlindBoxDTO {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("box_img")
    private String boxImg;
    private BigDecimal price;
    private String name;
    private String img;
    private Integer grade;
    @TableField("type_id")
    private Integer typeId;
    @TableField("gift_id")
    private int giftId;
    private Integer type;
    @TableField("sort_id")
    private Integer sortId;
    @TableField("add_time")
    private Date addTime;
    @TableField("update_time")
    private Date updateTime;
    private String typeName;
    private Integer productCount;
}
