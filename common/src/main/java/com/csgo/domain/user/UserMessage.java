package com.csgo.domain.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class UserMessage {

    @ApiModelProperty(value = "主键ID", required = false)
    private Integer id;

    @ApiModelProperty(value = "用户ID", required = true)
    private Integer userId;

    @ApiModelProperty(value = "游戏名称", required = true)
    private String gameName;

    @ApiModelProperty(value = "礼包类别", required = true)
    private String giftType;

    @ApiModelProperty(value = "商品名称", required = true)
    private String productName;

    @ApiModelProperty(value = "抽奖日期", required = true)
    private Date drawDare;

    @ApiModelProperty(value = "0-入库,1-已出售,2-已提取", required = true)
    private String state;

    @ApiModelProperty(value = "商品id", required = true)
    private Integer giftProductId;

    @ApiModelProperty(value = "背包状态", required = true)
    private String knapsackState;

    @ApiModelProperty(value = "商品金额", required = true)
    private BigDecimal money;

    @ApiModelProperty(value = "商品图片", required = true)
    private String img;

    @ApiModelProperty(value = "礼包状态", required = true)
    private String giftStatus;

    @ApiModelProperty(value = "轮次ID", required = true)
    private Integer turnId;

    @ApiModelProperty(value = "饰品类型", required = true)
    private Integer productKind;

    private Integer total;

    private Integer pageNum;

    private Integer pageSize;

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

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getGiftStatus() {
        return giftStatus;
    }

    public void setGiftStatus(String giftStatus) {
        this.giftStatus = giftStatus;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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

    public void setGiftProductId(Integer giftProductId) {
        this.giftProductId = giftProductId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName == null ? null : gameName.trim();
    }

    public String getGiftType() {
        return giftType;
    }

    public void setGiftType(String giftType) {
        this.giftType = giftType == null ? null : giftType.trim();
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName == null ? null : productName.trim();
    }

    public Date getDrawDare() {
        return drawDare;
    }

    public void setDrawDare(Date drawDare) {
        this.drawDare = drawDare;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state == null ? null : state.trim();
    }

    public Integer getGiftProductId() {
        return giftProductId;
    }

    public String getKnapsackState() {
        return knapsackState;
    }

    public void setKnapsackState(String knapsackState) {
        this.knapsackState = knapsackState == null ? null : knapsackState.trim();
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }
}
