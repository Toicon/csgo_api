package com.csgo.domain.plus.hee;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.csgo.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 汇付宝支付用户签约信息
 *
 * @author admin
 */
@Data
@TableName("user_pay_hee")
public class UserPayHee extends BaseEntity {
    //用户id
    @TableField(value = "user_id")
    private Integer userId;
    //商户签约单号
    @TableField(value = "out_trade_no")
    private String outTradeNo;
    //商户签约状态(0:签约中，1:已签约,2:签约失败)
    @ApiModelProperty(notes = "商户签约状态(0:签约中，1:已签约,2:签约失败)")
    @TableField(value = "trade_status")
    private Integer tradeStatus;
    //支付授权码
    @TableField(value = "sign_no")
    private String signNo;
    //持卡人预留银行手机号（脱敏）
    @ApiModelProperty(notes = "持卡人预留银行手机号（脱敏）")
    @TableField(value = "mobile")
    private String mobile;
    //持卡人身份证号（脱敏）
    @ApiModelProperty(notes = "持卡人身份证号（脱敏）")
    @TableField(value = "cert_no")
    private String certNo;
    //银行名称
    @ApiModelProperty(notes = "银行名称")
    @TableField(value = "bank_name")
    private String bankName;
    //持卡人银行卡号（脱敏）
    @ApiModelProperty(notes = "持卡人银行卡号（脱敏）")
    @TableField(value = "bank_card_no")
    private String bankCardNo;
    //持卡人姓名（脱敏）
    @ApiModelProperty(notes = "持卡人姓名（脱敏）")
    @TableField(value = "bank_user_name")
    private String bankUserName;
}
