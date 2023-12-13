package com.csgo.domain.plus.box;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.plus.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
@TableName("treasure_box")
public class TreasureBox extends BaseEntity {

    @TableField("name")
    private String name;
    @TableField("img")
    private String img;
    @TableField("halation_img")
    private String halationImg;
    @TableField("bg_img")
    private String bgImg;
}
