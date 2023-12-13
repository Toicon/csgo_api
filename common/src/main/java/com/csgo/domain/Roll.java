package com.csgo.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "roll", description = "roll对象信息")
public class Roll {
    @ApiModelProperty(value = "主键ID", required = false)
    private Integer id;

    @ApiModelProperty(value = "房间名称", required = true)
    private String rollname;

    @ApiModelProperty(value = "房间类型", required = true)
    private String rollType;

    @ApiModelProperty(value = "开奖方式,1-定时开奖", required = true)
    private String type;

    @ApiModelProperty(value = "开奖时间", required = true)
    private Date drawdate;

    @ApiModelProperty(value = "开奖时间", required = true)
    private Date startTime;

    @ApiModelProperty(value = "开奖时间", required = true)
    private Date endTime;

    @ApiModelProperty(value = "密码", required = true)
    private String password;

    @ApiModelProperty(value = "用户参与条件设置", required = true)
    private BigDecimal userlimit;

    @ApiModelProperty(value = "主播链接", required = true)
    private String anchorlink;

    @ApiModelProperty(value = "房间状态,0-进行中1-已结束", required = false)
    private String status;

    @ApiModelProperty(value = "修改时间", required = false)
    private Date ut;

    @ApiModelProperty(value = "创建时间", required = false)
    private Date ct;

    @ApiModelProperty(value = "中奖人数", required = true)
    private Integer num;

    @ApiModelProperty(value = "主播用户id", required = true)
    private Integer anchoruserid;

    @ApiModelProperty(value = "房间号码", required = true)
    private String rollnumber;

    @ApiModelProperty(value = "房间简介", required = true)
    private String rolldesc;

    @ApiModelProperty(value = "主播简介", required = true)
    private String anchordesc;

    @ApiModelProperty(value = "图片", required = true)
    private String img;

    @ApiModelProperty(value = "房间开关", required = true)
    private Boolean roomSwitch;

    private Integer usernum;

    private Integer totalGiftNum;

    private int pageSize;
    private int pageNum;

    private Integer total;

    private BigDecimal totalPrice;

    private List<RollGift> products;

    public Integer getUsernum() {
        return usernum;
    }

    public void setUsernum(Integer usernum) {
        this.usernum = usernum;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<RollGift> getProducts() {
        return products;
    }

    public void setProducts(List<RollGift> products) {
        this.products = products;
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

    public String getRolldesc() {
        return rolldesc;
    }

    public void setRolldesc(String rolldesc) {
        this.rolldesc = rolldesc;
    }

    public String getAnchordesc() {
        return anchordesc;
    }

    public void setAnchordesc(String anchordesc) {
        this.anchordesc = anchordesc == null ? null : anchordesc.trim();
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRollname() {
        return rollname;
    }

    public void setRollname(String rollname) {
        this.rollname = rollname == null ? null : rollname.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Date getDrawdate() {
        return drawdate;
    }

    public void setDrawdate(Date drawdate) {
        this.drawdate = drawdate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public BigDecimal getUserlimit() {
        return userlimit;
    }

    public void setUserlimit(BigDecimal userlimit) {
        this.userlimit = userlimit;
    }

    public String getAnchorlink() {
        return anchorlink;
    }

    public void setAnchorlink(String anchorlink) {
        this.anchorlink = anchorlink == null ? null : anchorlink.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
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

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getAnchoruserid() {
        return anchoruserid;
    }

    public void setAnchoruserid(Integer anchoruserid) {
        this.anchoruserid = anchoruserid;
    }

    public String getRollnumber() {
        return rollnumber;
    }

    public void setRollnumber(String rollnumber) {
        this.rollnumber = rollnumber == null ? null : rollnumber.trim();
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}