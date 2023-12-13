package com.csgo.web.response.membership;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Admin on 2021/12/14
 */
@Setter
@Getter
@ApiModel(value = "获取积分宝箱结果", description = "获取积分宝箱结果")
public class MembershipTaskBoxResponse {

    @ApiModelProperty(notes = "开宝箱所需积分", example = "500")
    private BigDecimal giftLevelLimit = BigDecimal.ZERO;

    @ApiModelProperty(notes = "开启宝箱所需ID", example = "1")
    private Integer giftId;

    @ApiModelProperty(notes = "宝箱图片地址", example = "https://vgcsgo.oss-cn-shenzhen.aliyuncs.com/admin/CTiNDlrXpX_DM_20210524203647_00120210524203727532")
    private String giftImg;

    @ApiModelProperty(notes = "是否能开启宝箱", example = "true")
    private boolean canOpen;
}
