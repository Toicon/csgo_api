package com.csgo.domain.plus.mine;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import lombok.Data;

/**
 * 扫雷随机饰品信息
 *
 * @author admin
 */
@Data
@TableName("mine_product")
public class MineProduct extends BaseEntity {
    //饰品信息
    @TableField(value = "gift_product_id")
    private Integer giftProductId;
}
