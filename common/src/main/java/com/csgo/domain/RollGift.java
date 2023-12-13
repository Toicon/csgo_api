package com.csgo.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RollGift {

    @ApiModelProperty(value = "主键ID", required = false)
    private Integer id;

    @ApiModelProperty(value = "商品id", required = true)
    private Integer giftProductId;

    @ApiModelProperty(value = "商品价格", required = true)
    private BigDecimal price;

    @ApiModelProperty(value = "商品名称", required = true)
    private String productname;

    @ApiModelProperty(value = "商品图片信息", required = true)
    private String img;

    @ApiModelProperty(value = "物品等级", required = true)
    private String grade;

    @ApiModelProperty(value = "roll房间id", required = true)
    private Integer rollid;

    @ApiModelProperty(value = "修改时间", required = true)
    private Date ut;

    @ApiModelProperty(value = "创建时间", required = true)
    private Date ct;

    private int pageSize;
    private int pageNum;

    private Integer total;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public Date getUt() {
        return ut;
    }

    public void setUt(Date ut) {
        this.ut = ut;
    }

    public Date getCt() {
        return ct;
    }

    public void setCt(Date ct) {
        this.ct = ct;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGiftProductId() {
        return giftProductId;
    }

    public void setGiftProductId(Integer giftProductId) {
        this.giftProductId = giftProductId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img == null ? null : img.trim();
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade == null ? null : grade.trim();
    }

    public Integer getRollid() {
        return rollid;
    }

    public void setRollid(Integer rollid) {
        this.rollid = rollid;
    }
}