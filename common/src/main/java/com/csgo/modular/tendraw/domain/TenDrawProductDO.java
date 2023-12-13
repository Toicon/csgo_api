package com.csgo.modular.tendraw.domain;

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
@TableName("ten_draw_game_product")
public class TenDrawProductDO extends BaseMybatisEntity<TenDrawProductDO> {

    @TableField("game_id")
    private Integer gameId;

    @ApiModelProperty(value = "奖励饰品ID")
    @TableField("product_id")
    private Integer productId;

    @ApiModelProperty(value = "奖励饰品名称")
    @TableField("product_name")
    private String productName;

    @ApiModelProperty(value = "奖励商品图片")
    @TableField("product_img")
    private String productImg;

    @ApiModelProperty(value = "奖励饰品价格")
    @TableField("product_price")
    private BigDecimal productPrice;

    @ApiModelProperty(value = "颜色")
    @TableField("color")
    private Integer color;

    @TableField(exist = false)
    private Integer weight;

}
