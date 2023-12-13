package com.csgo.web.request.stageProperty;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * Created by Admin on 2021/5/21
 */
@Setter
@Getter
public class ProductFilterCategoryRequest {
    private Integer id;

    @NotBlank(message = "关键字不能为空")
    private String key;

    @NotBlank(message = "分类名字不能为空")
    private String name;

    private Integer sortId;

    @NotBlank(message = "搜索关键字不能为空")
    private String searchKey;

    @NotBlank(message = "请上传分类图片")
    private String imgUrl;

    private String selfImg;

    private String parentKey = "0";
}
