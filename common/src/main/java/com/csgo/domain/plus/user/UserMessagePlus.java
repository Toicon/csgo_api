package com.csgo.domain.plus.user;

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
@TableName("user_message")
public class UserMessagePlus {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("user_id")
    private Integer userId;

    @TableField("game_name")
    private String gameName;
    @TableField("gift_type")
    private String giftType;

    @TableField("gift_product_id")
    private Integer giftProductId;
    @TableField("product_name")
    private String productName;
    private BigDecimal money;
    private String state;
    @TableField("draw_dare")
    private Date drawDare;

    private String img;
    @TableField("from_source")
    private Integer fromSource;

    @TableField("gift_status")
    private String giftStatus;
    @TableField("knapsack_state")
    private String knapsackState;
    @TableField("turn_id")
    private Integer turnId;
    @TableField("product_kind")
    private Integer productKind;

}
