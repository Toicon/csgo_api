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
@TableName("nov_bomb_game_product")
public class NovBombProductDO extends BaseMybatisEntity<NovBombProductDO> {

    @ApiModelProperty(value = "游戏id")
    @TableField("game_id")
    private Integer gameId;

    @TableField("play_id")
    private Integer playId;

    @ApiModelProperty(value = "商品下标")
    @TableField("product_index")
    private Integer productIndex;

    @ApiModelProperty(value = "掉落概率")
    @TableField("product_rate")
    private Integer productRate;

    @ApiModelProperty(value = "商品id")
    @TableField("product_id")
    private Integer productId;

    @ApiModelProperty(value = "商品图")
    @TableField("product_img")
    private String productImg;

    @ApiModelProperty(value = "商品名称")
    @TableField("product_name")
    private String productName;

    @ApiModelProperty(value = "商品金额")
    @TableField("product_price")
    private BigDecimal productPrice;

    @ApiModelProperty(value = "饰品标签")
    @TableField("product_exterior_name")
    private String productExteriorName;

    @TableField(exist = false)
    private Integer weight;

}
