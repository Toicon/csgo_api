package com.csgo.modular.system.model;

import com.beust.jcommander.internal.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author admin
 */
@Data
public class FrontSystemSettingVO {

    @ApiModelProperty(value = "检测版本")
    private String verifyVersion = "0";

    @ApiModelProperty(value = "用户事件")
    private List<String> eventList = Lists.newArrayList();

}
