package com.csgo.domain.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.JdbcType;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@TableName("user_message_gift")
public class UserMessageGift {

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
    @TableField(value = "ct", jdbcType = JdbcType.TIMESTAMP)
    private Date ct;
    @TableField(value = "ut", jdbcType = JdbcType.TIMESTAMP)
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
    private String steam;
    private String flag;
    @TableField("orderId")
    private String orderId;
    private Integer total;
    private Integer pageNum;
    private Integer pageSize;
    private Date st_time;
    private Date ed_time;

}