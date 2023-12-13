package com.csgo.domain.plus.gift;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.Gift;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@TableName(value = "gift_type")
@Setter
@Getter
public class GiftType {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("img")
    private String img;
    @TableField("name")
    private String name;

    @TableField("type")
    private String type;

    @TableField("hidden")
    private boolean hidden;

    @TableField("ct")
    private Date ct;

    @TableField("ut")
    private Date ut;

    @TableField("sort")
    private Integer sort;

    @TableField(exist = false)
    private List<Gift> giftList;
}