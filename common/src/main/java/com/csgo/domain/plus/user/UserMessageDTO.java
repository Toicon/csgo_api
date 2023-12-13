package com.csgo.domain.plus.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Data
public class UserMessageDTO {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("user_id")
    private Integer userId;
    @TableField("game_name")
    private String gameName;
    @TableField("gift_type")
    private String giftType;
    @TableField("product_name")
    private String productName;
    @TableField("draw_dare")
    private Date drawDare;
    @TableField("state")
    private String state;
    @TableField("gift_product_id")
    private Integer giftProductId;
    @TableField("knapsack_state")
    private String knapsackState;
    @TableField("money")
    private BigDecimal money;
    @TableField("img")
    private String img;
    @TableField("gift_status")
    private String giftStatus;
    @TableField("exterior_name")
    private String exteriorName;

}
