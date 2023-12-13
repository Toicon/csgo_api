package com.csgo.framework.mybatis.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.framework.model.PageParam;
import com.csgo.framework.model.PageVO;
import com.csgo.framework.model.SortingField;
import com.echo.framework.platform.web.request.PageRequest;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author admin
 */
public class MyBatisUtils {

    public static <T> Page<T> buildPage(PageRequest pageParam) {
        return new Page<>(pageParam.getPageIndex(), pageParam.getPageSize());
    }

    public static <T> Page<T> buildPage(PageParam pageParam) {
        return buildPage(pageParam, null);
    }

    public static <T> Page<T> buildPage(PageParam pageParam, Collection<SortingField> sortingFields) {
        // 页码 + 数量
        Page<T> page = new Page<>(pageParam.getPageIndex(), pageParam.getPageSize());
        // 排序字段
        if (!CollectionUtils.isEmpty(sortingFields)) {
            page.addOrder(sortingFields.stream().map(sortingField -> SortingField.ORDER_ASC.equals(sortingField.getOrder()) ?
                            OrderItem.asc(sortingField.getField()) : OrderItem.desc(sortingField.getField()))
                    .collect(Collectors.toList()));
        }
        return page;
    }

    public static <E> PageVO<E> toPageVO(IPage<E> iPage) {
        return toPageVO(iPage, iPage.getRecords());
    }

    public static <E> PageVO<E> toPageVO(IPage<?> iPage, IPage<E> resultPage) {
        return toPageVO(iPage, resultPage.getRecords());
    }

    public static <E> PageVO<E> toPageVO(IPage<?> iPage, List<E> rows) {
        PageVO<E> vo = new PageVO<>();
        vo.setTotal(iPage.getTotal());
        vo.setPageIndex(iPage.getCurrent());
        vo.setPageSize(iPage.getSize());
        vo.setRows(rows);
        return vo;
    }

}
