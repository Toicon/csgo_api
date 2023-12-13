package com.csgo.domain.plus.accessory;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author admin
 */
@Getter
@Setter
@TableName("sys_lucky_product_type")
public class LuckyProductType {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    @TableField("name")
    private String name;
    @TableField("img")
    private String img;
    @TableField("sort_id")
    private Integer sortId;
    @TableField("ct")
    private Date ct;
    @TableField("ut")
    private Date ut;
}
