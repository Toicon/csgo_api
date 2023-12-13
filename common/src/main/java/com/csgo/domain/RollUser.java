package com.csgo.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RollUser {

    @ApiModelProperty(value = "主键ID", required = false)
    private Integer id;

    @ApiModelProperty(value = "用户id", required = true)
    private Integer userid;

    @ApiModelProperty(value = "房间id", required = true)
    private Integer rollId;

    @ApiModelProperty(value = "用户头像", required = true)
    private String img;

    @ApiModelProperty(value = "是否指定用户,0-不是1-指定用户", required = true)
    private String isappoint;

    @ApiModelProperty(value = "roll房间名称", required = true)
    private String rollname;

    @ApiModelProperty(value = "用户昵称", required = true)
    private String username;

    @ApiModelProperty(value = "是否中奖", required = true)
    private String flag;

    @ApiModelProperty(value = "修改时间", required = true)
    private Date ut;

    @ApiModelProperty(value = "创建时间", required = true)
    private Date ct;

    @ApiModelProperty(value = "中奖商品", required = true)
    private Integer rollgiftId;

    @ApiModelProperty(value = "中奖商品图片信息", required = true)
    private String rollgiftImg;

    @ApiModelProperty(value = "中奖商品名称", required = true)
    private String rollgiftName;

    @ApiModelProperty(value = "中奖商品价格", required = true)
    private BigDecimal rollgiftPrice;

    @ApiModelProperty(value = "中奖商品等级", required = true)
    private String rollgiftGrade;

    private int pageSize;
    private int pageNum;

    private Integer total;

    public String getRollgiftGrade() {
        return rollgiftGrade;
    }

    public void setRollgiftGrade(String rollgiftGrade) {
        this.rollgiftGrade = rollgiftGrade;
    }

    public String getRollgiftName() {
        return rollgiftName;
    }

    public void setRollgiftName(String rollgiftName) {
        this.rollgiftName = rollgiftName;
    }

    public BigDecimal getRollgiftPrice() {
        return rollgiftPrice;
    }

    public void setRollgiftPrice(BigDecimal rollgiftPrice) {
        this.rollgiftPrice = rollgiftPrice;
    }

    public Integer getRollgiftId() {
        return rollgiftId;
    }

    public void setRollgiftId(Integer rollgiftId) {
        this.rollgiftId = rollgiftId;
    }

    public String getRollgiftImg() {
        return rollgiftImg;
    }

    public void setRollgiftImg(String rollgiftImg) {
        this.rollgiftImg = rollgiftImg;
    }

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

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getRollId() {
        return rollId;
    }

    public void setRollId(Integer rollId) {
        this.rollId = rollId;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img == null ? null : img.trim();
    }

    public String getIsappoint() {
        return isappoint;
    }

    public void setIsappoint(String isappoint) {
        this.isappoint = isappoint == null ? null : isappoint.trim();
    }

    public String getRollname() {
        return rollname;
    }

    public void setRollname(String rollname) {
        this.rollname = rollname == null ? null : rollname.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag == null ? null : flag.trim();
    }
}