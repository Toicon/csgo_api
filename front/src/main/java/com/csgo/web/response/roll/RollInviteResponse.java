package com.csgo.web.response.roll;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 2021/6/20
 */
@Setter
@Getter
public class RollInviteResponse {
    @ApiModelProperty(notes = "推广码", example = "11111")
    private String inviteCode;
    @ApiModelProperty(notes = "邀请人昵称", example = "aaaaa")
    private String nickname;
    @ApiModelProperty(notes = "包含奖品", example = "数组")
    private List<RollInviteGiftResponse> giftResponses = new ArrayList<>();
}
