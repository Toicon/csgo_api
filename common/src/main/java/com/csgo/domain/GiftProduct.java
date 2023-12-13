package com.csgo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class GiftProduct implements Cloneable {

    @ApiModelProperty(value = "主键ID", required = false)
    private Integer id;

    @ApiModelProperty(value = " 商品类别(礼包类别)", required = true)
    private String type;

    @ApiModelProperty(value = "价格", required = true)
    private BigDecimal price;

    @ApiModelProperty(value = "原价", required = true)
    private BigDecimal originAmount;

    @ApiModelProperty(value = "商品名称", required = true)
    private String name;

    @ApiModelProperty(value = "商品图片", required = true)
    private String img;

    @ApiModelProperty(value = "创建时间", required = true)
    private Date createdAt;

    @ApiModelProperty(value = "修改时间", required = true)
    private Date updatedAt;

    @ApiModelProperty(value = "礼包id", required = true)
    private Integer giftId;

    @ApiModelProperty(value = "内部概率设置", required = true)
    private String withinProbability;

    @ApiModelProperty(value = "外部概率设置", required = true)
    private String outProbability;

    @ApiModelProperty(value = "游戏名称", required = true)
    private String gameName;

    @ApiModelProperty(value = "设为封面 0 不设  1 设置", required = true)
    private String isdefault;

    @ApiModelProperty(value = "商品简介")
    private String content;

    @ApiModelProperty(value = "物品等级")
    private String grade;

    @ApiModelProperty(value = "商品英文名称")
    private String englishName;

    @ApiModelProperty(value = "饰品id")
    private String itemId;

    @ApiModelProperty(value = "csgo对应的商品类型字段", required = true)
    private String csgoType;

    @ApiModelProperty(value = "csgo对应的商品类型字段", required = true)
    private String csgoTypeName;

    @ApiModelProperty(value = "对应崭新出厂这样的标签", required = true)
    private String exteriorName;

    private String imgUrl;

    private Integer pageNum;

    private Integer pageSize;

    private Integer userMessageId;

    private Integer total;

    private String show_probability;

    private Integer sortId;

    private Integer luckyId;

    @ApiModelProperty(value = "是否推荐")
    private Integer isRecommend;

    @ApiModelProperty(value = "类别Id")
    private Integer typeId;

    @ApiModelProperty(value = "饰品类别")
    private String typeName;

    @ApiModelProperty(value = "幸运饰品概率")
    private Double probability;
    @ApiModelProperty(value = "幸运饰品数据ID")
    private Integer randomId;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date updateTime;

    private Integer productKind;

    @ApiModelProperty(value = "中奖概率")
    private BigDecimal winRate;
    @JsonIgnore
    private BigDecimal winWeight;

    public String getShow_probability() {
        return show_probability;
    }

    public void setShow_probability(String show_probability) {
        this.show_probability = show_probability;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Integer getUserMessageId() {
        return userMessageId;
    }

    public void setUserMessageId(Integer userMessageId) {
        this.userMessageId = userMessageId;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIsdefault() {
        return isdefault;
    }

    public void setIsdefault(String isdefault) {
        this.isdefault = isdefault;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img == null ? null : img.trim();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getGiftId() {
        return giftId;
    }

    public void setGiftId(Integer giftId) {
        this.giftId = giftId;
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

    @Override
    public String toString() {
        return "GiftProduct{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", price=" + price +
                ", name='" + name + '\'' +
                ", img='" + img + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", giftId=" + giftId +
                ", withinProbability='" + withinProbability + '\'' +
                ", outProbability='" + outProbability + '\'' +
                ", gameName='" + gameName + '\'' +
                ", isdefault='" + isdefault + '\'' +
                '}';
    }
}
