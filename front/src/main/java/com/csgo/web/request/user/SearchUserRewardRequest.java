package com.csgo.web.request.user;

import com.echo.framework.platform.web.request.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 奖励查询
 *
 * @author admin
 */
@Getter
@Setter
public class SearchUserRewardRequest extends PageRequest {
    @ApiModelProperty(value = "奖励类别 （1：CDK奖励 2:红包奖励 3、其他奖励）", required = true)
    private Integer rewardType;
}
