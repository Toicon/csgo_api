package com.csgo.support;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "商品筛选条件", description = "商品筛选条件")
public class ProductFilters implements Serializable {
    private String appId;

    private String appName;

    private String icon;

    private String iconLarge;

    private List<ProductFiltersDetails> list;
}
