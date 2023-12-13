package com.csgo.domain.plus.membership;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.plus.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <p>VIP等级表</p>
 * Created by abel_huang on 2021/12/12
 */
@Setter
@Getter
@TableName(value = "membership_level_config")
public class MembershipLevelConfig extends BaseEntity {
    /**
     * 等级
     */
    @TableField("level")
    private Integer level;
    /**
     * 等级上限
     */
    @TableField("level_limit")
    private BigDecimal levelLimit;
    /**
     * 头像图标
     */
    @TableField("img")
    private String img;
    /**
     * roll福利
     */
    @TableField("roll_name")
    private String rollName;
    /**
     * 充值加赠百分率
     */
    @TableField("gift_rate")
    private BigDecimal giftRate;

}
