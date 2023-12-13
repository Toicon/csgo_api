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
@TableName("nov_bomb_game")
public class NovBombGameDO extends BaseMybatisEntity<NovBombGameDO> {

    @ApiModelProperty(value = "uuid")
    @TableField("uuid")
    private String uuid;

    @ApiModelProperty(value = "配置id")
    @TableField("config_id")
    private Integer configId;

    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    private Integer userId;

    @ApiModelProperty(value = "状态(0:进行中，1:已结束)")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "支付金额")
    @TableField("price")
    private BigDecimal price;

    @ApiModelProperty(value = "总金额")
    @TableField("sum_amount")
    private BigDecimal sumAmount;

    @ApiModelProperty(value = "抽奖次数")
    @TableField("play_times")
    private Integer playTimes;

    @ApiModelProperty(value = "最低值")
    @TableField("low_price")
    private BigDecimal lowPrice;

    @ApiModelProperty(value = "最高值")
    @TableField("high_price")
    private BigDecimal highPrice;

    @ApiModelProperty(value = "最大次数")
    @TableField("max_times")
    private Integer maxTimes;

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

    @ApiModelProperty(value = "商品下标")
    @TableField("reward_product_index")
    private Integer rewardProductIndex;

    @ApiModelProperty(value = "饰品标签")
    @TableField("reward_product_exterior_name")
    private String rewardProductExteriorName;

    @ApiModelProperty(value = "领取时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("reward_date")
    private Date rewardDate;

}
