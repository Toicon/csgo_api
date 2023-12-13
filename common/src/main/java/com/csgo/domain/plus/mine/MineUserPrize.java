package com.csgo.domain.plus.mine;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 扫雷活动用户闯关信息
 *
 * @author admin
 */
@Data
@TableName("mine_user_prize")
public class MineUserPrize extends BaseEntity {
    //用户id
    @TableField(value = "user_id")
    private Integer userId;
    //活动id
    @TableField(value = "activity_id")
    private Integer activityId;
    //账号
    @TableField(value = "user_name")
    private String userName;
    //奖品商品id
    @TableField(value = "gift_product_id")
    private Integer giftProductId;
    //奖励名称
    @TableField(value = "prize_name")
    private String prizeName;
    //奖励价格
    @TableField(value = "prize_price")
    private BigDecimal prizePrice;
    //支付金额
    @TableField(value = "pay_price")
    private BigDecimal payPrice;

}
