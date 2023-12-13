package com.csgo.condition;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author admin
 */
public abstract class Condition<T> {

    private Page<T> page;

    private String sort;

    private String sortBy;

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Page<T> getPage() {
        return page;
    }

    public void setPage(Page<T> page) {
        this.page = page;
    }
}
