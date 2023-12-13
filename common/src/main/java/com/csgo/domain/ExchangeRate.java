package com.csgo.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 添加客服信息
 */
@Data
public class ExchangeRate {

    @ApiModelProperty(value = "主键ID", required = false)
    private Integer id;

    @ApiModelProperty(value = "汇率", required = false)
    private String exchangeRate;

    @ApiModelProperty(value = "0-禁止 1-启用", required = true)
    private Integer flag;

    @ApiModelProperty(value = "创建时间", required = true)
    private Date ct;

    @ApiModelProperty(value = "修改时间", required = true)
    private Date ut;

    @ApiModelProperty(value = "设置提取金额", required = false)
    private BigDecimal extractMoney;

    @ApiModelProperty(value = "提取zbk的最大金额的浮动", required = false)
    private String upsAndDowns;

    @ApiModelProperty(value = "支付赠送初始金额", required = false)
    private BigDecimal pay_give_money;

    @ApiModelProperty(value = "溢值", required = false)
    private BigDecimal spill_price;


    @ApiModelProperty(value = "一级佣金比例", required = false)
    private BigDecimal firstCommission;


    @ApiModelProperty(value = "二级佣金比例", required = false)
    private BigDecimal secondCommission;

    @ApiModelProperty(value = "幸运值", required = false)
    private Integer luckyValue;

    public BigDecimal getSpill_price() {
        return spill_price;
    }

    public void setSpill_price(BigDecimal spill_price) {
        this.spill_price = spill_price;
    }

    public BigDecimal getPay_give_money() {
        return pay_give_money;
    }

    public void setPay_give_money(BigDecimal pay_give_money) {
        this.pay_give_money = pay_give_money;
    }

    public String getUpsAndDowns() {
        return upsAndDowns;
    }

    public void setUpsAndDowns(String upsAndDowns) {
        this.upsAndDowns = upsAndDowns;
    }

    public BigDecimal getExtractMoney() {
        return extractMoney;
    }

    public void setExtractMoney(BigDecimal extractMoney) {
        this.extractMoney = extractMoney;
    }

    private Integer pageNum;

    private Integer pageSize;

    private Integer total;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(String exchangeRate) {
        this.exchangeRate = exchangeRate == null ? null : exchangeRate.trim();
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public Date getCt() {
        return ct;
    }

    public void setCt(Date ct) {
        this.ct = ct;
    }

    public Date getUt() {
        return ut;
    }

    public void setUt(Date ut) {
        this.ut = ut;
    }
}