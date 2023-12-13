package com.csgo.domain.plus.fish;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 钓鱼用户获得奖励
 */
@Data
@TableName("fish_user_prize")
public class FishUserPrize extends BaseEntity {
    //用户id
    @TableField(value = "user_id")
    private Integer userId;
    //礼包id
    @TableField(value = "gift_id")
    private Integer giftId;
    //活动id
    @TableField(value = "activity_id")
    private Integer activityId;
    //支付金额
    @TableField(value = "pay_price")
    private BigDecimal payPrice;
    //第几轮
    @TableField(value = "turn")
    private Integer turn;
    //账号
    @TableField(value = "user_name")
    private String userName;
    //奖品商品id
    @TableField(value = "gift_product_id")
    private Integer giftProductId;
    //奖励名称
    @TableField(value = "gift_product_name")
    private String giftProductName;
    //奖励图片
    @TableField(value = "gift_product_image")
    private String giftProductImage;
    //奖励价格
    @TableField(value = "gift_product_price")
    private BigDecimal giftProductPrice;
    //颜色等级
    @TableField(value = "out_probability")
    private Integer outProbability;
    //奖励提示状态(0:未提示，1:已提示)
    @TableField(value = "prompt_state")
    private Integer promptState;

}
