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
@ApiModel(value = "幸运饰品关联商品表", description = "幸运饰品")
//sys_lucky_product
@Entity
@Table(name = "sys_lucky_product")
//@Repository//LuckyProductRepository
public class LuckyProductDO implements Serializable {

    @ApiModelProperty(value = "主键ID", required = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty(value = "商品表对应的id,gift_product_id", required = false)
    @Column(name = "gift_product_id")
    private Integer giftProductId;

    @ApiModelProperty(value = "排序，sort_id", required = false)
    @Column(name = "sort_id")
    private Integer sortId;

    @ApiModelProperty(value = "道具图片", required = false)
    @Column(name = "img_url")
    private String imgUrl;

    @ApiModelProperty(value = "价格", required = true)
    @Column(name = "price")
    private BigDecimal price;

    @ApiModelProperty(value = "是否推荐", required = true)
    @Column(name = "is_recommend")
    private Integer isRecommend;

    @ApiModelProperty(value = "类型Id", required = true)
    @Column(name = "type_id")
    private Integer typeId;

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
    @ApiModelProperty(value = "标签", required = false)
    @Transient
    private String exteriorName;
    @ApiModelProperty(value = "道具英文名称", required = false)
    @Transient
    private String englishName;
    @ApiModelProperty(value = "用户选择的百分比", required = false)
    @Transient
    private Double userChoice;

}
