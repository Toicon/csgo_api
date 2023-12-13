package com.csgo.support;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "商品筛选条件详情", description = "商品筛选条件详情")
public class ProductFiltersDetails implements Serializable {

    @ApiModelProperty("关键字")
    private String key;

    @ApiModelProperty("名字")
    private String name;

    @ApiModelProperty("搜索关键字")
    private String searchKey;

    @ApiModelProperty("图片")
    private String image;

    @ApiModelProperty("详情")
    private List<ProductFiltersDetails> list;
}
