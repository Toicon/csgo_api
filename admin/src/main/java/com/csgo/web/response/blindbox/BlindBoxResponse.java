package com.csgo.web.response.blindbox;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
public class BlindBoxResponse {

    @ApiModelProperty(value = "主键ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty(value = "盲盒名称", required = true)
    private String name;

    @ApiModelProperty(value = "盲盒类型Id", required = true)
    @Column(name = "type_id")
    private Integer typeId;

    @ApiModelProperty(value = "1 自定义 2 从礼包中选择的", required = true)
    private Integer type;

    @ApiModelProperty(value = "排序", required = true)
    @Column(name = "sort_id")
    private Integer sortId;

    @ApiModelProperty(value = "价格", required = true)
    private BigDecimal price;

    /**
     * 1钻石白 2钻石紫 3灰色 4 红色 5 紫色 6 绿色
     */
    @ApiModelProperty(value = "稀有度（123456六个等级 等级越低越稀有）", required = true)
    private Integer grade;

    @ApiModelProperty(value = "盲盒展示图", required = true)
    private String img;

    @ApiModelProperty(value = "盲盒宝箱图", required = true)
    private String boxImg;

    @ApiModelProperty("新增时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "add_time")
    private Date addTime;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private Date updateTime;
    @Transient
    @ApiModelProperty(value = "盲盒类别名称", required = true)
    private String typeName;
    @Transient
    @ApiModelProperty(value = "盲盒商品数", required = true)
    private Integer productCount;
    private Integer giftId;
    private BigDecimal threshold;
}
