package com.csgo.modular.tendraw.domain;

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
@TableName("ten_draw_ball")
public class TenDrawBallDO extends BaseMybatisEntity<TenDrawBallDO> {

    @TableField("name")
    private String name;

    @TableField("img")
    private String img;

    @TableField("weight")
    private BigDecimal weight;

}
