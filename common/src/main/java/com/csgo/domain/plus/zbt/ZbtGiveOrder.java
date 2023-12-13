package com.csgo.domain.plus.zbt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Admin on 2021/7/18
 */
@TableName("zbt_give_order")
@Setter
@Getter
public class ZbtGiveOrder {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    @TableField("out_trade_no")
    private String outTradeNo;

    @TableField("name")
    private String name;

    @TableField("item_id")
    private String itemId;

    @TableField("price")
    private BigDecimal price;

    @TableField("type")
    private ZbtGiveOrderType type;

    @TableField("status")
    private ZbtGiveOrderStatus status;

    @TableField("msg")
    private String msg;

    @TableField("steam_url")
    private String steamUrl;

    @TableField("order_id")
    private String orderId;

    @TableField("nick_name")
    private String nickName;

    @TableField("avatar")
    private String avatar;

    @TableField("ct")
    private Date ct;

    @TableField("cb")
    private String cb;
}
