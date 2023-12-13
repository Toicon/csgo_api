package com.csgo.modular.bomb.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.framework.mybatis.entity.BaseMybatisEntity;
import io.swagger.annotations.ApiModelProperty;
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
@TableName("nov_bomb_config")
public class NovBombConfigDO extends BaseMybatisEntity<NovBombConfigDO> {

    @ApiModelProperty(value = "名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "排序值")
    @TableField("sort_id")
    private Integer sortId;

    @ApiModelProperty(value = "金额")
    @TableField("price")
    private BigDecimal price;

    @ApiModelProperty(value = "最低值")
    @TableField("low_price")
    private BigDecimal lowPrice;

    @ApiModelProperty(value = "最高值")
    @TableField("high_price")
    private BigDecimal highPrice;

    @ApiModelProperty(value = "最大次数")
    @TableField("max_times")
    private Integer maxTimes;

    @ApiModelProperty(value = "饰品倍场参数")
    @TableField("product_price_params")
    private BigDecimal productPriceParams;

    @ApiModelProperty(value = "掉落概率")
    @TableField("rate1")
    private Integer rate1;

    @ApiModelProperty(value = "掉落概率")
    @TableField("rate2")
    private Integer rate2;

    @ApiModelProperty(value = "掉落概率")
    @TableField("rate3")
    private Integer rate3;

    @ApiModelProperty(value = "掉落概率")
    @TableField("rate4")
    private Integer rate4;

    @ApiModelProperty(value = "掉落概率")
    @TableField("rate5")
    private Integer rate5;

    @ApiModelProperty(value = "是否隐藏")
    @TableField("hidden")
    private Boolean hidden;

}
