package com.csgo.web.request.face;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 人脸核身地址请求参数
 */
@Data
public class AliFaceRequest {
    /**
     * 订单号，字母/数字组成的字符串，由合作方上送，每次唯一，不能超过32位
     */
    private String orderNo;
    /**
     * 姓名
     */
    @ApiModelProperty(notes = "真实姓名")
    private String name;
    /**
     * 身份证号码
     */
    @ApiModelProperty(notes = "身份证号码")
    private String idNo;
    /**
     * 用户 ID ，用户的唯一标识（不要带有特殊字符）
     */
    private String userId;

    /**
     * 认证单据号，人脸核身结果查询
     */
    private String certifyId;
}
