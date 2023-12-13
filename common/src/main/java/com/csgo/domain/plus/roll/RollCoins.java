package com.csgo.domain.plus.roll;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Admin on 2021/5/11
 */
@TableName("roll_coins")
@Setter
@Getter
public class RollCoins {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("roll_id")
    private Integer rollId;

    @TableField("user_id")
    private Integer userId;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("img")
    private String img;

    @TableField("ct")
    private Date ct;
}
