package com.csgo.domain.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "幸运饰品历史记录", description = "幸运饰品历史记录")
public class UserLuckyHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键ID", required = false)
    private Integer id;

    @ApiModelProperty(value = "编号", required = true)
    private String luckNumber;

    @ApiModelProperty(value = "用户ID", required = true)
    private Integer userId;

    @ApiModelProperty(value = "概率  百分比", required = true)
    private Integer probability;

    @ApiModelProperty(value = "幸运饰品关联id", required = true)
    private Integer luckyId;

    @ApiModelProperty(value = "价格", required = true)
    private BigDecimal price;

    @ApiModelProperty(value = "随机饰品名称", required = true)
    private String randomProductName;

    @ApiModelProperty(value = "随机饰品图片", required = true)
    private String randomProductImg;

    @ApiModelProperty(value = "随机饰品ID", required = true)
    private Integer randomProductId;

    @ApiModelProperty(value = "随机饰品价格", required = true)
    private BigDecimal randomProductPrice;

    @ApiModelProperty(value = "幸运饰品名称", required = true)
    private String luckyProductName;

    @ApiModelProperty(value = "幸运饰品Id", required = true)
    private Integer luckyProductId;

    @ApiModelProperty(value = "幸运饰品图片", required = true)
    private String luckyProductImg;

    @ApiModelProperty(value = "幸运饰品价格", required = true)
    private BigDecimal luckyProductPrice;

    @ApiModelProperty(value = "是否抽中", required = true)
    private Integer isLucky;


    @ApiModelProperty("新增时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "add_time")
    private Date addTime;


    @Transient
    private String img;

    @Transient
    private String name;

    @Transient
    @ApiModelProperty(value = "随机商品背包ID", required = true)
    private Integer randomMessageId;

    @Transient
    @ApiModelProperty(value = "幸运商品背包ID", required = true)
    private Integer luckyMessageId;

}