package com.csgo.modular.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.framework.mybatis.entity.BaseMybatisEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author admin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("system_setting")
public class SystemSettingDO extends BaseMybatisEntity<SystemSettingDO> {

    @TableField("name")
    private String name;

    @TableField("config_key")
    private String configKey;

    @TableField("config_value")
    private String configValue;

    @TableField("visible")
    private Boolean visible;

    @TableField("remark")
    private String remark;

}
