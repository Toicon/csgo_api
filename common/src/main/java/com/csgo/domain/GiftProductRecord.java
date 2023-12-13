package com.csgo.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品记录
 */
public class GiftProductRecord {
    private Integer id;

    private Integer giftId;

    private Integer giftProductId;

    private String withinProbability;

    private String outProbability;

    private String show_probability;

    private int weight;

    private String isdefault;

    private Date ct;

    private Date ut;

    private Boolean specialState;

    private BigDecimal probabilityPrice;

    private GiftProduct giftProduct;

    private Integer pageNum;

    private Integer pageSize;

    private Integer total;

    private Integer num;

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getShow_probability() {
        return show_probability;
    }

    public void setShow_probability(String show_probability) {
        this.show_probability = show_probability;
    }

    public GiftProduct getGiftProduct() {
        return giftProduct;
    }

    public void setGiftProduct(GiftProduct giftProduct) {
        this.giftProduct = giftProduct;
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

    public Integer getGiftId() {
        return giftId;
    }

    public void setGiftId(Integer giftId) {
        this.giftId = giftId;
    }

    public Integer getGiftProductId() {
        return giftProductId;
    }

    public void setGiftProductId(Integer giftProductId) {
        this.giftProductId = giftProductId;
    }

    public String getWithinProbability() {
        return withinProbability;
    }

    public void setWithinProbability(String withinProbability) {
        this.withinProbability = withinProbability == null ? null : withinProbability.trim();
    }

    public String getOutProbability() {
        return outProbability;
    }

    public void setOutProbability(String outProbability) {
        this.outProbability = outProbability == null ? null : outProbability.trim();
    }

    public String getIsdefault() {
        return isdefault;
    }

    public void setIsdefault(String isdefault) {
        this.isdefault = isdefault == null ? null : isdefault.trim();
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

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Boolean getSpecialState() {
        return specialState;
    }

    public void setSpecialState(Boolean specialState) {
        this.specialState = specialState;
    }

    public BigDecimal getProbabilityPrice() {
        return probabilityPrice;
    }

    public void setProbabilityPrice(BigDecimal probabilityPrice) {
        this.probabilityPrice = probabilityPrice;
    }
}
