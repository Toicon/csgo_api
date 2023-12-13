package com.csgo.domain.plus.fish;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 钓鱼活动鱼饵配置信息
 */
@Data
@TableName("fish_bait_config")
public class FishBaitConfig extends BaseEntity {
    //场次类型(1:初级，2:中级，3:高级)
    @TableField(value = "session_type")
    @NotNull(message = "场次类型不能为空")
    private Integer sessionType;
    //鱼饵名称
    @TableField(value = "bait_name")
    @NotBlank(message = "鱼饵名称不能为空")
    private String baitName;
    //鱼饵图片地址
    @TableField(value = "bait_img")
    private String baitImg;
    //鱼饵背景图片地址
    @TableField(value = "bait_bg_img")
    private String baitBgImg;
    //支付倍数
    @TableField(value = "pay_ratio")
    @NotNull(message = "支付倍数不能为空")
    private BigDecimal payRatio;
    //开箱次数
    @TableField(value = "open_box")
    @NotNull(message = "开箱次数不能为空")
    private Integer openBox;
}
