package com.csgo.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 公告
 */
@Data
public class Notice {
    @ApiModelProperty(value = "主键ID", required = false)
    private Integer id;

    @ApiModelProperty(value = "标题", required = true)
    private String title;

    @ApiModelProperty(value = "是否展示,0-不展示,1-展示", required = true)
    private Integer flag;

    @ApiModelProperty(value = "内容", required = true)
    private String content;

    @ApiModelProperty(value = "创建时间", required = true)
    private Date ct;

    @ApiModelProperty(value = "修改时间", required = true)
    private Date ut;

    private Integer total;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}