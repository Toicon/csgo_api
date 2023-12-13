package com.csgo.domain.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class UserPrize {

    @ApiModelProperty(value = "主键ID", required = false)
    private Integer id;

    @ApiModelProperty(value = "用户id", required = true)
    private Integer userId;

    @ApiModelProperty(value = "用户名称", required = true)
    private String userName;

    @ApiModelProperty(value = "用户头像", required = true)
    private String userImg;

    @ApiModelProperty(value = "用户名称全称", required = true)
    private String userNameQ;

    @ApiModelProperty(value = "游戏名称", required = true)
    private String gameName;

    @ApiModelProperty(value = "礼包id", required = true)
    private Integer giftId;

    @ApiModelProperty(value = "礼包name", required = true)
    private String giftName;

    @ApiModelProperty(value = "礼包类型", required = true)
    private String giftType;

    @ApiModelProperty(value = "礼包商品id", required = true)
    private String giftProductId;

    @ApiModelProperty(value = "礼包商品名称", required = true)
    private String giftProductName;

    @ApiModelProperty(value = "礼包商品img", required = true)
    private String giftProductImg;

    @ApiModelProperty(value = "礼包商品price", required = true)
    private BigDecimal price;

    @ApiModelProperty(value = "中奖时间", required = true)
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date ct;

    @ApiModelProperty(value = "礼包内道具等级", required = true)
    private String gift_grade;

    @ApiModelProperty(value = "礼包price", required = true)
    private BigDecimal giftprice;

    @ApiModelProperty(value = "礼包等级", required = true)
    private String gift_grade_g;

    @ApiModelProperty(value = "开箱密码", required = true)
    private String gift_password;

    private int pageNum;

    private int pageSize;

    private Integer total;

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getGift_grade_g() {
        return gift_grade_g;
    }

    public void setGift_grade_g(String gift_grade_g) {
        this.gift_grade_g = gift_grade_g;
    }

    public String getGift_password() {
        return gift_password;
    }

    public void setGift_password(String gift_password) {
        this.gift_password = gift_password;
    }

    public BigDecimal getGiftprice() {
        return giftprice;
    }

    public void setGiftprice(BigDecimal giftprice) {
        this.giftprice = giftprice;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getUserNameQ() {
        return userNameQ;
    }

    public void setUserNameQ(String userNameQ) {
        this.userNameQ = userNameQ;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getGift_grade() {
        return gift_grade;
    }

    public void setGift_grade(String gift_grade) {
        this.gift_grade = gift_grade;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName == null ? null : gameName.trim();
    }

    public Integer getGiftId() {
        return giftId;
    }

    public void setGiftId(Integer giftId) {
        this.giftId = giftId;
    }

    public String getGiftType() {
        return giftType;
    }

    public void setGiftType(String giftType) {
        this.giftType = giftType == null ? null : giftType.trim();
    }

    public String getGiftProductId() {
        return giftProductId;
    }

    public void setGiftProductId(String giftProductId) {
        this.giftProductId = giftProductId == null ? null : giftProductId.trim();
    }

    public String getGiftProductName() {
        return giftProductName;
    }

    public void setGiftProductName(String giftProductName) {
        this.giftProductName = giftProductName == null ? null : giftProductName.trim();
    }

    public String getGiftProductImg() {
        return giftProductImg;
    }

    public void setGiftProductImg(String giftProductImg) {
        this.giftProductImg = giftProductImg == null ? null : giftProductImg.trim();
    }

    public Date getCt() {
        return ct;
    }

    public void setCt(Date ct) {
        this.ct = ct;
    }
}