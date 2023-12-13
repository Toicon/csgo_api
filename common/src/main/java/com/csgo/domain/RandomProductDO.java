package com.csgo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "随机饰品关联商品表", description = "随机饰品")
@Entity
@Table(name = "sys_random_product")
public class RandomProductDO implements Serializable {

    @ApiModelProperty(value = "主键ID", required = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty(value = "商品表对应的id,gift_product_id", required = false)
    @Column(name = "gift_product_id")
    private Integer giftProductId;

    @ApiModelProperty(value = "幸运饰品表对应的id,lucky_id", required = true)
    @Column(name = "lucky_id")
    private Integer luckyId;

    @ApiModelProperty(value = "中奖概率，probability", required = true)
    @Column(name = "probability")
    private Double probability;

    @ApiModelProperty(value = "道具图片", required = false)
    @Column(name = "img_url")
    private String imgUrl;

    @ApiModelProperty(value = "价格", required = true)
    @Column(name = "price")
    private BigDecimal price;


    @ApiModelProperty("新增时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "add_time")
    private java.util.Date addTime;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private java.util.Date updateTime;


    /*===========================================================================*/

    @ApiModelProperty(value = "道具名称", required = false)
    @Transient
    private String productName;

}
