package com.csgo.domain;

import java.math.BigDecimal;
import java.util.Date;

public class CodeInfo {
    private Integer id;

    private String code;

    private String holder;

    private String remarks;

    private Integer userNum;

    private BigDecimal payMoney;

    private BigDecimal payMoneyX;

    private BigDecimal extract;

    private BigDecimal extractX;

    private Date ct;

    private Date ut;

    private int total;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder == null ? null : holder.trim();
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public Integer getUserNum() {
        return userNum;
    }

    public void setUserNum(Integer userNum) {
        this.userNum = userNum;
    }

    public BigDecimal getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(BigDecimal payMoney) {
        this.payMoney = payMoney;
    }

    public BigDecimal getPayMoneyX() {
        return payMoneyX;
    }

    public void setPayMoneyX(BigDecimal payMoneyX) {
        this.payMoneyX = payMoneyX;
    }

    public BigDecimal getExtract() {
        return extract;
    }

    public void setExtract(BigDecimal extract) {
        this.extract = extract;
    }

    public BigDecimal getExtractX() {
        return extractX;
    }

    public void setExtractX(BigDecimal extractX) {
        this.extractX = extractX;
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