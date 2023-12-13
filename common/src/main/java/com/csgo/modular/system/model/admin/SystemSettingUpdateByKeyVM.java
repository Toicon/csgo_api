package com.csgo.modular.system.model.admin;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author admin
 */
@Data
public class SystemSettingUpdateByKeyVM {

    @NotNull(message = "配置Key不能为空")
    private String configKey;

    @NotNull(message = "配置Value不能为空")
    private String configValue;

}
