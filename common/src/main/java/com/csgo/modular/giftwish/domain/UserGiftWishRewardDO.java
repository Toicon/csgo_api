package com.csgo.modular.giftwish.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.framework.mybatis.entity.BaseMybatisEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("user_gift_wish_reward")
public class UserGiftWishRewardDO extends BaseMybatisEntity<UserGiftWishDO> {

    @TableField("user_id")
    private Integer userId;

    @TableField("wish_id")
    private Integer wishId;

    @TableField("gift_product_id")
    private Integer giftProductId;

    @TableField("gift_product_img")
    private String giftProductImg;

    @TableField("gift_product_name")
    private String giftProductName;

    @TableField("gift_product_price")
    private BigDecimal giftProductPrice;

}
