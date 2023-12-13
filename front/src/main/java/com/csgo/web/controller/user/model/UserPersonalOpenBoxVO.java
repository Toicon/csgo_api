package com.csgo.web.controller.user.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author admin
 */
@Data
@NoArgsConstructor
public class UserPersonalOpenBoxVO {

    @ApiModelProperty(value = "主键ID", required = false)
    private Integer userId;

    @ApiModelProperty(value = "今日开箱次数")
    private Integer openBoxCountToday = 0;

    @ApiModelProperty(value = "本周开箱次数")
    private Integer openBoxCountWeek = 0;

    @ApiModelProperty(value = "本月开箱次数")
    private Integer openBoxCountMonth = 0;

}
