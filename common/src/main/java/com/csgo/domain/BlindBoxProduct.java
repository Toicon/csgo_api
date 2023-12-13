package com.csgo.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "盲盒表", description = "盲盒")
@Entity
@Table(name = "blind_box_product")
public class BlindBoxProduct {
    @ApiModelProperty(value = "主键ID", required = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty(value = "盲盒Id", required = true)
    @Column(name = "blind_box_id")
    private Integer blindBoxId;

    @ApiModelProperty(value = "商品ID", required = true)
    @Column(name = "gift_product_id")
    private Integer giftProductId;

    @ApiModelProperty(value = "商品图", required = true)
    @Column(name = "img_url")
    private String imgUrl;

    @ApiModelProperty(value = "价格", required = true)
    private BigDecimal price;

    @ApiModelProperty(value = "概率", required = true)
    private Double probability;

    @ApiModelProperty(value = "抽奖权重", required = true)
    @Column(name = "weight")
    private int weight;

    @ApiModelProperty(value = "奖池等级", required = true)
    @Column(name = "out_probability")
    private int outProbability;

    @ApiModelProperty("新增时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "add_time")
    private Date addTime;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private Date updateTime;


    @Transient
    @ApiModelProperty(value = "商品名称", required = true)
    private String productName;

    @Transient
    @ApiModelProperty(value = "id集合", required = true)
    private List<Integer> ids;

}
