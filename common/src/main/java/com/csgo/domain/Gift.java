package com.csgo.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.csgo.domain.plus.gift.GiftType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Gift {

    @ApiModelProperty(value = "主键ID", required = false)
    private Integer id;

    @ApiModelProperty(value = "礼包类别", required = true)
    private String type;

    @ApiModelProperty(value = "礼包类别id", required = true)
    private Integer typeId;

    @ApiModelProperty(value = "单个价格", required = true)
    private BigDecimal price;

    @ApiModelProperty(value = "礼包名称", required = true)
    private String name;

    @ApiModelProperty(value = "礼包内奖品默认图片", required = true)
    private String img;

    @ApiModelProperty(value = "创建时间", required = true)
    private Date createdAt;

    @ApiModelProperty(value = "修改时间", required = true)
    private Date updatedAt;

    @ApiModelProperty(value = "礼包等级id", required = true)
    private String grade;

    @ApiModelProperty(value = "隐藏", required = true)
    private Boolean hidden;

    @ApiModelProperty(value = "展示掉落概率", required = true)
    private String showProbability;

    private Integer boxId;

    private int pageNum;

    private int pageSize;

    private Integer total;

    @ApiModelProperty(value = "散户抽奖的总次数", required = true)
    private Integer count;

    @ApiModelProperty(value = "内部用户抽奖的总次数", required = true)
    private Integer countwithin;

    private BigDecimal threshold;

    private Integer membershipGrade;

    private Boolean wishSwitch;

    private Boolean newPeopleSwitch;

    private Integer keyProductId;
    @ApiModelProperty(value = "钥匙饰品名称")
    private String keyProductName;

    @ApiModelProperty(value = "钥匙数量")
    private Integer keyNum;
    @ApiModelProperty(value = "是否产生钥匙")
    private Boolean keyGenerator = Boolean.FALSE;

    @ApiModelProperty(value = "用户钥匙数量")
    private Integer userKeyNum;

    public BigDecimal getThreshold() {
        return threshold;
    }

    public void setThreshold(BigDecimal threshold) {
        this.threshold = threshold;
    }

    public Integer getCountwithin() {
        return countwithin;
    }

    public void setCountwithin(Integer countwithin) {
        this.countwithin = countwithin;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    private GiftType giftType;

    private GiftGrade giftGrade;

    @ApiModelProperty(value = "开箱密码", required = true)
    private String gift_password;


    public String getGift_password() {
        return gift_password;
    }

    public void setGift_password(String gift_password) {
        this.gift_password = gift_password;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public GiftType getGiftType() {
        return giftType;
    }

    public void setGiftType(GiftType giftType) {
        this.giftType = giftType;
    }

    public GiftGrade getGiftGrade() {
        return giftGrade;
    }

    public void setGiftGrade(GiftGrade giftGrade) {
        this.giftGrade = giftGrade;
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

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade == null ? null : grade.trim();
    }
}
