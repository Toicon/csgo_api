package com.csgo.framework.util;

import com.csgo.framework.model.PageVO;

import java.util.function.Function;

/**
 * @author admin
 */
public class PageUtil {

    public static <R, T> PageVO<R> map(PageVO<T> page, Function<T, R> mapper) {
        PageVO<R> result = new PageVO<>();
        for (T record : page.getRows()) {
            result.getRows().add(mapper.apply(record));
        }
        result.setPageIndex(page.getPageIndex());
        result.setPageSize(page.getPageSize());
        result.setTotal(page.getTotal());
        return result;
    }

}
