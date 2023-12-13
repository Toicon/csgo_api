package com.csgo.support.ZBT;

import java.util.List;

public class ZBTDate {

    private Integer limit;

    private List<ZBTBean> list;

    private Integer page;

    private Integer pages;

    private Integer total;

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public List<ZBTBean> getList() {
        return list;
    }

    public void setList(List<ZBTBean> list) {
        this.list = list;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "data{" +
                "limit=" + limit +
                ", list=" + list +
                ", page=" + page +
                ", pages=" + pages +
                ", total=" + total +
                '}';
    }
}
