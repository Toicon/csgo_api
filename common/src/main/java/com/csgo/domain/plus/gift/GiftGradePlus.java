package com.csgo.domain.plus.gift;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("gift_grade")
public class GiftGradePlus {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String img;
    private String grade;
    private Date ct;
    private Date ut;
}