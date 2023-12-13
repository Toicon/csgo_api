package com.csgo.modular.tendraw.domain;

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
@TableName("ten_draw_game")
public class TenDrawGameDO extends BaseMybatisEntity<TenDrawGameDO> {

    @TableField("uuid")
    private String uuid;

    @TableField("user_id")
    private Integer userId;

    @TableField("state")
    private Integer state;

    @TableField("pay_price")
    private BigDecimal payPrice;

    @TableField("retry_price")
    private BigDecimal retryPrice;

    @TableField("sum_amount")
    private BigDecimal sumAmount;

    @TableField("play_times")
    private Integer playTimes;

    @TableField("max_play_times")
    private Integer maxPlayTimes;

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "reward_date")
    private Date rewardDate;

    @TableField("blue_rate")
    private Integer blueRate;

    @TableField("red_rate")
    private Integer redRate;

    @TableField("yellow_rate")
    private Integer yellowRate;

}
