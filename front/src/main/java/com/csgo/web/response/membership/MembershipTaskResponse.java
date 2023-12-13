package com.csgo.web.response.membership;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Admin on 2021/12/14
 */
@Setter
@Getter
@ApiModel(value = "获取积分任务结果", description = "获取积分任务结果")
public class MembershipTaskResponse {

    @ApiModelProperty(notes = "当前积分", example = "300")
    private BigDecimal currentGrowth;

    @ApiModelProperty(notes = "下一级所需积分", example = "500")
    private BigDecimal levelLimit;

    @ApiModelProperty(notes = "获取积分宝箱结果", example = "获取积分宝箱结果")
    private List<MembershipTaskBoxResponse> boxList;

    @ApiModelProperty(notes = "获取积分列表结果", example = "获取积分列表结果")
    private List<MembershipTaskInfoResponse> taskInfoList;
}
