package com.csgo.domain.plus.mine;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 扫雷奖励价格区间设置
 *
 * @author admin
 */
@Data
@TableName("mine_user_config_price")
public class MineUserConfigPrice extends BaseEntity {
    //用户id
    @TableField(value = "user_id")
    private Integer userId;
    //起始金额
    @TableField(value = "min_price")
    private BigDecimal minPrice;
    //截止金额
    @TableField(value = "max_price")
    private BigDecimal maxPrice;
}
