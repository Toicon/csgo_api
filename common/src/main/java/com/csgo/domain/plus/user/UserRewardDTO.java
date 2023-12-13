package com.csgo.domain.plus.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author admin
 */
@Data
public class UserRewardDTO {

    @ApiModelProperty(value = "奖励类别 （1：CDK奖励 2:红包奖励 3、其他奖励）")
    private Integer rewardType;
    @ApiModelProperty(value = "图片")
    private String rewardImg;
    @ApiModelProperty(value = "名称")
    private String rewardName;
    @ApiModelProperty(value = "领取时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date rewardDate;
}
