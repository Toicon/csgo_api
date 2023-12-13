package com.csgo.domain.plus.config;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.plus.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@TableName("system_config")
@Getter
@Setter
public class SystemConfig extends BaseEntity {
    @TableField("`key`")
    private String key;
    private String value;
    private String description;
}
