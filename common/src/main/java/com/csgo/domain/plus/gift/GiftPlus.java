package com.csgo.domain.plus.gift;

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
@TableName("gift")
public class GiftPlus {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String type;
    @TableField("type_id")
    private Integer typeId;
    private BigDecimal price;
    private String name;
    private String img;
    @TableField("created_at")
    private Date ceatedAt;
    @TableField("updated_at")
    private Date updatedAt;
    @TableField("gift_password")
    private String giftPassword;
    @TableField("show_probability")
    private String showProbability;
    private String grade;
    private Boolean hidden;
    private Integer countwithin;
    private BigDecimal threshold;
    @TableField("membership_grade")
    private Integer membershipGrade;
    @TableField("wish_switch")
    private Boolean wishSwitch;
    @TableField("new_people_switch")
    private Boolean newPeopleSwitch;
    @TableField("probability_type")
    private Integer probabilityType;
    @TableField("key_product_id")
    private Integer keyProductId;
    @TableField("key_num")
    private Integer keyNum;
}
