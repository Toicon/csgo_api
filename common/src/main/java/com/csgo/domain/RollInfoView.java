package com.csgo.domain;

import com.csgo.domain.response.RollGiftResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author admin
 */
public class RollInfoView {
    private List<RollGiftResponse> giftResponses;
    private int totalGiftNum;
    private BigDecimal totalPrice;
    private BigDecimal diamondTotalPrice;
    private int userNum;

    public List<RollGiftResponse> getGiftResponses() {
        return giftResponses;
    }

    public void setGiftResponses(List<RollGiftResponse> giftResponses) {
        this.giftResponses = giftResponses;
    }

    public int getTotalGiftNum() {
        return totalGiftNum;
    }

    public void setTotalGiftNum(int totalGiftNum) {
        this.totalGiftNum = totalGiftNum;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getUserNum() {
        return userNum;
    }

    public void setUserNum(int userNum) {
        this.userNum = userNum;
    }

    public BigDecimal getDiamondTotalPrice() {
        return diamondTotalPrice;
    }

    public void setDiamondTotalPrice(BigDecimal diamondTotalPrice) {
        this.diamondTotalPrice = diamondTotalPrice;
    }
}
