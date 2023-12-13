package com.csgo.web.request.membership;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * Created by Admin on 2022/6/26
 */
@Setter
@Getter
@ApiModel(value = "更换会员头像", description = "更换会员头像")
public class EditMembershipImgRequest {

    @NotBlank(message = "img is required")
    @ApiModelProperty(notes = "头像框地址", example = "头像框地址")
    private String img;
}
