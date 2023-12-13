package com.csgo.modular.giftwish.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.framework.mybatis.entity.BaseMybatisEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author admin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("user_gift_wish")
public class UserGiftWishDO extends BaseMybatisEntity<UserGiftWishDO> {

    public static final Integer STATE_ING = 0;
    public static final Integer STATE_RECEIVE = 1;
    public static final Integer STATE_CANCEL = 2;
    public static final Integer STATE_SYSTEM_CANCEL = 3;

    @TableField("user_id")
    private Integer userId;

    @TableField("gift_id")
    private Integer giftId;

    @TableField(value = "gift_product_id")
    private Integer giftProductId;

    @TableField("wish_current")
    private Integer wishCurrent;

    @TableField("wish_total")
    private Integer wishTotal;

    @TableField("state")
    private Integer state;

}
