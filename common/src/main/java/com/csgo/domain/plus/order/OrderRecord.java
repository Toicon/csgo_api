package com.csgo.domain.plus.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@TableName("order_record")
@Setter
@Getter
public class OrderRecord {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("order_num")
    private String orderNum;

    @TableField("order_status")
    private String orderStatus;

    @TableField("order_amount")
    private BigDecimal orderAmount;

    @TableField("paid_amount")
    private BigDecimal paidAmount;

    @TableField("extra_price")
    private BigDecimal extraPrice;

    @TableField("user_id")
    private Integer userId;

    @TableField("user_phone")
    private String userPhone;

    @TableField("style")
    private OrderRecordStyle style;

    @TableField("create_time")
    private Date createTime;

    @TableField("channel_order_num")
    private String channelOrderNum;

    @TableField("is_first")
    private boolean first;

    @TableField("update_time")
    private Date updateTime;

    @TableField("paid_time")
    private Date paidTime;

    //父类id(主播用户)
    @TableField("parent_id")
    private Integer parentId;
}
