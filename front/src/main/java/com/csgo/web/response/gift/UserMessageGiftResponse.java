package com.csgo.web.response.gift;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class UserMessageGiftResponse {

    @ApiModelProperty(value = "主键ID", required = false)
    private Integer id;
    @ApiModelProperty(value = "用户背包信息ID", required = true)
    private Integer userMessageId;
    @ApiModelProperty(value = "商品ID", required = true)
    private Integer giftProductId;
    @ApiModelProperty(value = "商品状态 （1：出售  2：已提取 3:提取中）", required = true)
    private Integer state;
    @ApiModelProperty(value = "商品价格", required = true)
    private BigDecimal money;
    @ApiModelProperty(value = "出售金额", required = true)
    private BigDecimal sellMoney;

    @ApiModelProperty(value = "ZBT购买的价格", required = true)
    private BigDecimal zbkMoney;

    @ApiModelProperty(value = "图片信息", required = true)
    private String img;
    @ApiModelProperty(value = "用户手机号", required = true)
    private String phone;
    @ApiModelProperty(value = "用户ID", required = true)
    private Integer userId;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间", required = true)
    private Date ct;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "修改时间", required = true)
    private Date ut;
    @ApiModelProperty(value = "提取的ZBT的订单时间", required = true)
    private String zbkDate;

    @ApiModelProperty(value = "游戏名称", required = true)
    private String gameName;
    @ApiModelProperty(value = "礼包类型", required = true)
    private String giftType;
    @ApiModelProperty(value = "礼包商品名称", required = true)
    private String giftProductName;
    @ApiModelProperty(value = "交易链接", required = true)
    private String transactionlink;
    @ApiModelProperty(value = "steam账号", required = true)
    private String steam;

    @ApiModelProperty(value = "0 外部用户  1 内部用户", required = true)
    private String flag;

    @ApiModelProperty(value = "提取的ZBT的订单id", required = true)
    private String orderId;
    private String giftGradeG;
}