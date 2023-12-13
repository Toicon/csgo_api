package com.csgo.support;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import lombok.Data;


@Data
@ApiModel(value = "商品筛选条件返回结果", description = "商品筛选条件返回结果")
public class ZbtProductFiltersReponse implements Serializable {

    private Boolean success;

    private ProductFilters data;
}
