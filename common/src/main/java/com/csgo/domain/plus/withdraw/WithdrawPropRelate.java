package com.csgo.domain.plus.withdraw;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.enums.WithdrawDeliveryMethod;
import com.csgo.domain.enums.WithdrawPropItemStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
@TableName("withdraw_pop_relate")
public class WithdrawPropRelate {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    @TableField("pop_id")
    private int popId;
    @TableField("message_id")
    private int messageId;
    @TableField("out_trade_no")
    private String outTradeNno;
    private String orderId;
    private BigDecimal zbtPrice;
    @TableField("status")
    private WithdrawPropItemStatus status;
    private String message;

    @TableField("status_msg")
    private String statusMsg;

    private String steamUrl;
    @TableField("delivery_method")
    private WithdrawDeliveryMethod deliveryMethod;
    private String cb;
    private String ub;
    private Date ct;
    private Date ut;
}
