package com.csgo.web.response.mine;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 扫雷-获取奖励信息
 */
@Data
public class MineUserLastDropResponse {

    /**
     * 奖励id
     */
    @ApiModelProperty(notes = "奖励id")
    private Integer id;
    /**
     * 用户昵称
     */
    @ApiModelProperty(notes = "用户昵称")
    private String nickName;
    /**
     * 用户头像
     */
    @ApiModelProperty(notes = "用户头像")
    private String userImg;
    /**
     * 奖品名称
     */
    @ApiModelProperty(notes = "奖品名称")
    private String giftProductName;
    /**
     * 奖品图片
     */
    @ApiModelProperty(notes = "奖品图片")
    private String giftProductImg;
    /**
     * 奖品商品价格
     */
    @ApiModelProperty(value = "奖品商品价格")
    private BigDecimal giftProductPrice;


}
