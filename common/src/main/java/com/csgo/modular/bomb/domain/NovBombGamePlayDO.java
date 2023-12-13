package com.csgo.modular.bomb.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.framework.mybatis.entity.BaseMybatisEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("nov_bomb_game_play")
public class NovBombGamePlayDO extends BaseMybatisEntity<NovBombGamePlayDO> {

    @ApiModelProperty(value = "游戏id")
    @TableField("game_id")
    private Integer gameId;

    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    private Integer userId;

    @ApiModelProperty(value = "状态(0:进行中，1:已结束)")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "挑战类型")
    @TableField("play_type")
    private Integer playType;

    @ApiModelProperty(value = "支付金额")
    @TableField("price")
    private BigDecimal price;

    @ApiModelProperty(value = "选择线路")
    @TableField("choose_index")
    private Integer chooseIndex;

    @ApiModelProperty(value = "奖励商品id")
    @TableField("reward_product_id")
    private Integer rewardProductId;

    @ApiModelProperty(value = "奖励商品图")
    @TableField("reward_product_img")
    private String rewardProductImg;

    @ApiModelProperty(value = "奖励商品名称")
    @TableField("reward_product_name")
    private String rewardProductName;

    @ApiModelProperty(value = "奖励商品金额")
    @TableField("reward_product_price")
    private BigDecimal rewardProductPrice;

    @ApiModelProperty(value = "饰品标签")
    @TableField("reward_product_exterior_name")
    private String rewardProductExteriorName;

    @ApiModelProperty(value = "商品下标")
    @TableField("reward_product_index")
    private Integer rewardProductIndex;

    @ApiModelProperty(value = "领取时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("reward_date")
    private Date rewardDate;

}
