package com.csgo.web.response.stageProperty;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 2021/5/21
 */
@Setter
@Getter
public class ProductFilterCategoryPlusResponse {
    private Integer id;

    private String key;

    private String name;

    private Integer sortId;

    private String searchKey;

    private String imgUrl;

    private String selfImg;

    private String parentKey = "0";

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private java.util.Date addTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private java.util.Date updateTime;
}
