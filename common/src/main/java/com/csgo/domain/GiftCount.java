package com.csgo.domain;

import java.util.Date;

public class GiftCount {
    private Integer id;

    private Integer giftId;

    //二级礼包次数
    private Integer counttwo;

    //三级礼包次数
    private Integer countthree;

    //四级礼包次数
    private Integer countfour;

    //5级礼包次数
    private Integer countfive;

    //二级礼包次数
    private Integer counttwoWithin;

    //三级礼包次数
    private Integer countthreeWithin;

    //四级礼包次数
    private Integer countfourWithin;

    //5级礼包次数
    private Integer countfiveWithin;

    private Date ct;

    private Date ut;

    public Integer getCounttwoWithin() {
        return counttwoWithin;
    }

    public void setCounttwoWithin(Integer counttwoWithin) {
        this.counttwoWithin = counttwoWithin;
    }

    public Integer getCountthreeWithin() {
        return countthreeWithin;
    }

    public void setCountthreeWithin(Integer countthreeWithin) {
        this.countthreeWithin = countthreeWithin;
    }

    public Integer getCountfourWithin() {
        return countfourWithin;
    }

    public void setCountfourWithin(Integer countfourWithin) {
        this.countfourWithin = countfourWithin;
    }

    public Integer getCountfiveWithin() {
        return countfiveWithin;
    }

    public void setCountfiveWithin(Integer countfiveWithin) {
        this.countfiveWithin = countfiveWithin;
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

    public Integer getCountfive() {
        return countfive;
    }

    public void setCountfive(Integer countfive) {
        this.countfive = countfive;
    }

    public Integer getCounttwo() {
        return counttwo;
    }

    public void setCounttwo(Integer counttwo) {
        this.counttwo = counttwo;
    }

    public Integer getCountthree() {
        return countthree;
    }

    public void setCountthree(Integer countthree) {
        this.countthree = countthree;
    }

    public Integer getCountfour() {
        return countfour;
    }

    public void setCountfour(Integer countfour) {
        this.countfour = countfour;
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