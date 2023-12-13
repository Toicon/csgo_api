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
@ApiModel(value = "生日登记请求接口", description = "生日登记请求接口")
public class EditMembershipRequest {

    @NotBlank(message = "idCard is required")
    @ApiModelProperty(notes = "身份证号", example = "身份证号码")
    private String idCard;

    @NotBlank(message = "realName is required")
    @ApiModelProperty(notes = "真实名称", example = "真实名称，大于两位，小于4位")
    private String realName;
}
