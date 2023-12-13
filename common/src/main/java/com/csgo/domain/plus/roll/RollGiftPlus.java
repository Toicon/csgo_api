package com.csgo.domain.plus.roll;

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
@TableName("roll_gift")
public class RollGiftPlus {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("giftProductId")
    private Integer giftProductId;
    @TableField("price")
    private BigDecimal price;
    @TableField("productname")
    private String productname;
    @TableField("img")
    private String img;
    @TableField("grade")
    private String grade;
    @TableField("rollId")
    private Integer rollId;
    @TableField("type")
    private RollGiftType type;
    @TableField("ut")
    private Date ut;
    @TableField("ct")
    private Date ct;
}