package com.csgo.domain.plus.ali;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 支付宝小程序菜单表
 *
 * @author admin
 */
@Data
@TableName("ali_app_menu")
public class AliAppMenu extends BaseEntity {
    //菜单编码(1:购买记录2:提货记录3:待发货4:账单流水5:退出账号)
    @ApiModelProperty(notes = "菜单编码(1:购买记录2:提货记录3:待发货4:账单流水5:退出账号)")
    @TableField(value = "menu_code")
    private Integer menuCode;
    //菜单名称
    @ApiModelProperty(notes = "菜单名称")
    @TableField(value = "menu_name")
    private String menuName;
    //是否隐藏(0:否,1:是)
    @TableField(value = "display_state")
    @JsonIgnore
    private Integer displayState;
}
