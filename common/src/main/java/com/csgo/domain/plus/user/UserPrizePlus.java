package com.csgo.domain.plus.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@TableName("user_prize")
public class UserPrizePlus {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("user_id")
    private Integer userId;
    @TableField("user_name")
    private String userName;
    @TableField("userNameQ")
    private String userNameQ;
    @TableField("game_name")
    private String gameName;
    @TableField("gift_id")
    private Integer giftId;
    @TableField("gift_type")
    private String giftType;
    @TableField("gift_product_id")
    private Integer giftProductId;
    @TableField("gift_product_name")
    private String giftProductName;
    @TableField("gift_product_img")
    private String giftProductImg;
    private BigDecimal price;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date ct;
    @TableField("gift_grade")
    private String giftGrade;
    @TableField("giftprice")
    private BigDecimal giftPrice;
    @TableField("gift_grade_g")
    private String giftGradeG;
    @TableField("giftName")
    private String giftName;
    @TableField("csgo_type")
    private String csgoType;
}
