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
@TableName("ten_draw_game_play")
public class TenDrawGamePlayDO extends BaseMybatisEntity<TenDrawGamePlayDO> {

    @TableField("game_id")
    private Integer gameId;

    @TableField("user_id")
    private Integer userId;

    @TableField("play_price")
    private BigDecimal playPrice;

    @TableField("play_num")
    private Integer playNum;

    @TableField("play_index")
    private Integer playIndex;

    @ApiModelProperty(value = "奖励饰品ID")
    @TableField("reward_product_id")
    private Integer rewardProductId;

    @ApiModelProperty(value = "奖励饰品名称")
    @TableField("reward_product_name")
    private String rewardProductName;

    @ApiModelProperty(value = "奖励商品图片")
    @TableField("reward_product_img")
    private String rewardProductImg;

    @ApiModelProperty(value = "奖励饰品价格")
    @TableField("reward_product_price")
    private BigDecimal rewardProductPrice;

    @ApiModelProperty(value = "奖励序号")
    @TableField("reward_index")
    private Integer rewardIndex;

    @ApiModelProperty(value = "颜色")
    @TableField("color")
    private Integer color;

}
