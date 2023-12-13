package com.csgo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "饰品分类,道具分类", description = "饰品分类,道具分类")
public class ProductFilterCategoryDO implements Serializable {

    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("关键字")
    @NotBlank(message = "关键字不能为空")
    private String key;

    @ApiModelProperty("名字")
    @NotBlank(message = "分类名字不能为空")
    private String name;


    @ApiModelProperty("排序,默认99")
    private Integer sortId;

    @ApiModelProperty("搜索关键字")
    @NotBlank(message = "搜索关键字不能为空")
    private String searchKey;

    @ApiModelProperty("分类图片")
    @NotBlank(message = "请上传分类图片")
    private String imgUrl;

    @ApiModelProperty("自定义图片")
    private String selfImg;

    @ApiModelProperty("父级key，如果没有父级取值为0")
    private String parentKey = "0";

    @ApiModelProperty("新增时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date addTime;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date updateTime;

    @ApiModelProperty("二级分类")
    private List<ProductFilterCategoryDO> childList;

    private Integer typeId;

}
