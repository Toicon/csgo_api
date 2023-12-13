package com.csgo.domain.plus.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@TableName("user_message_gift")
public class UserMessageGiftPlus {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("user_message_id")
    private Integer userMessageId;
    @TableField("gift_product_id")
    private Integer giftProductId;
    @TableField("state")
    private Integer state;
    @TableField("money")
    private BigDecimal money;
    @TableField("sell_money")
    private BigDecimal sellMoney;
    @TableField("zbk_money")
    private BigDecimal zbkMoney;
    @TableField("img")
    private String img;
    @TableField("phone")
    private String phone;
    @TableField("user_id")
    private Integer userId;
    @TableField("ct")
    private Date ct;
    @TableField("ut")
    private Date ut;
    @TableField("zbk_date")
    private String zbkDate;
    @TableField("game_name")
    private String gameName;
    @TableField("gift_type")
    private String giftType;
    @TableField("gift_product_name")
    private String giftProductName;
    @TableField("transactionlink")
    private String transactionlink;
    @TableField("orderId")
    private String orderId;

}