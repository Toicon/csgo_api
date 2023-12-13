package com.csgo.web.response.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class UserMessageItemRecordResponse {

    @ApiModelProperty(value = "主键ID", required = false)
    private Integer id;

    @ApiModelProperty(value = "记录ID", required = true)
    private Integer recordId;

    @ApiModelProperty(value = "用户背包ID", required = true)
    private Integer userMessageId;

    @ApiModelProperty(value = "商品图片", required = true)
    private String img;
}
