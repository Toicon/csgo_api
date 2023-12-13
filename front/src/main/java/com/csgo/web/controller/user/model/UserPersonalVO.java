package com.csgo.web.controller.user.model;

import com.csgo.domain.user.UserLuckyHistoryLowProbabilityDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author admin
 */
@Data
@NoArgsConstructor
public class UserPersonalVO {

    @ApiModelProperty(value = "主键ID", required = false)
    private Integer userId;

    @ApiModelProperty(value = "昵称", required = true)
    private String userName;

    @ApiModelProperty(value = "用户头像", required = true)
    private String userImg;

    @ApiModelProperty(value = "已陪伴天数")
    private Integer onlineDayCount = 0;

    @ApiModelProperty(value = "最低升级概率饰品")
    private UserLuckyHistoryLowProbabilityDTO lowProbabilityProduct = null;

    @ApiModelProperty(value = "今日开箱获得价格")
    private BigDecimal openBoxRewardMoneyToday = BigDecimal.ZERO;

}
