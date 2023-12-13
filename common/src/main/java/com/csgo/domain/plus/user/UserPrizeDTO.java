package com.csgo.domain.plus.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class UserPrizeDTO {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("user_id")
    private Integer userId;
    @TableField("user_name")
    private String userName;
    private String userNameQ;
    @TableField("game_name")
    private String gameName;
    @TableField("gift_id")
    private Integer giftId;
    @TableField("gift_type")
    private String giftType;
    @TableField("gift_product_id")
    private String giftProductId;
    @TableField("gift_product_name")
    private String giftProductName;
    @TableField("gift_product_img")
    private String giftProductImg;
    private String csgoType;
    private BigDecimal price;
    private Date ct;
    @TableField("gift_grade")
    private String giftGrade;
    @TableField("giftprice")
    private BigDecimal giftPrice;
    @TableField("gift_grade_g")
    private String giftGradeG;
    private String giftName;
    private String userImg;
}
