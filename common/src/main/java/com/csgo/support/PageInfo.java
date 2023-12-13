package com.csgo.support;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * @author admin
 */
public class PageInfo<T> {
    private int pageNum;
    private int pageSize;
    private int total;
    private int pages;
    private List<T> list;

    public PageInfo() {
    }

    public PageInfo(Page<T> page) {
        this.pageNum = (int) page.getCurrent();
        this.pageSize = (int) page.getSize();
        this.total = (int) page.getTotal();
        this.pages = (int) page.getPages();
        this.list = page.getRecords();
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getPageNum() {
        return this.pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPages() {
        return this.pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

}
