package com.csgo.modular.bomb.model.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author admin
 */
@Data
public class AdminNovBombConfigCreateVM {

    @NotBlank(message = "名称不能为空")
    @Length(max = 32, message = "不能超过32个字符")
    private String name;

    @ApiModelProperty(value = "排序值")
    @NotNull(message = "排序值不能为空")
    private Integer sortId;

    @ApiModelProperty(value = "金额")
    @NotNull(message = "金额不能为空")
    private BigDecimal price;

    @ApiModelProperty(value = "最低值")
    @NotNull(message = "最低值不能为空")
    private BigDecimal lowPrice;

    @ApiModelProperty(value = "最高值")
    @NotNull(message = "最高值不能为空")
    private BigDecimal highPrice;

    @ApiModelProperty(value = "最高值")
    @NotNull(message = "最大次数不能为空")
    private Integer maxTimes;

    @ApiModelProperty(value = "饰品倍场参数")
    @NotNull(message = "饰品倍场参数不能为空")
    private BigDecimal productPriceParams;

    @ApiModelProperty(value = "显示概率")
    @NotNull(message = "显示概率1不能为空")
    private Integer rate1;

    @ApiModelProperty(value = "显示概率")
    @NotNull(message = "显示概率2不能为空")
    private Integer rate2;

    @ApiModelProperty(value = "显示概率")
    @NotNull(message = "显示概率3不能为空")
    private Integer rate3;

    @ApiModelProperty(value = "显示概率")
    @NotNull(message = "显示概率4不能为空")
    private Integer rate4;

    @ApiModelProperty(value = "显示概率")
    @NotNull(message = "显示概率5不能为空")
    private Integer rate5;

    @ApiModelProperty(value = "隐藏", required = true)
    private Boolean hidden;

}
