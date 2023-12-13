package com.csgo.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "返回的分页数据列表", description = "返回的分页数据列表")
public class PageResult<T> implements Serializable {

    @ApiModelProperty("总条数")
    private long count;

    @ApiModelProperty("当前页")
    private Integer page;

    @ApiModelProperty("每页条数")
    private Integer size;

    @ApiModelProperty("返回的数据")
    private List<T> data;
}
