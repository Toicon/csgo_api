package com.csgo.domain.plus.stageProperty;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 2021/5/21
 */
@TableName("sys_product_filter_category")
@Setter
@Getter
public class ProductFilterCategoryPlus {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("`key`")
    private String key;

    @TableField("name")
    private String name;


    @TableField("sort_id")
    private Integer sortId;

    @TableField("search_key")
    private String searchKey;

    @TableField("img_url")
    private String imgUrl;

    @TableField("self_img")
    private String selfImg;

    @TableField("parent_key")
    private String parentKey = "0";

    @TableField("add_time")
    private java.util.Date addTime;

    @TableField("update_time")
    private java.util.Date updateTime;
}
