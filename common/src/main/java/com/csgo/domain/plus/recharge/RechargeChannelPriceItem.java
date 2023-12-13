package com.csgo.domain.plus.recharge;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.plus.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author admin
 */
@TableName("recharge_channel_price_item")
@Getter
@Setter
public class RechargeChannelPriceItem extends BaseEntity {

    @TableField("channel_id")
    private int channelId;
    @TableField("goods_id")
    private Integer goodsId;
    @TableField("price")
    private BigDecimal price;
    @TableField("extra_price")
    private BigDecimal extraPrice;
}
