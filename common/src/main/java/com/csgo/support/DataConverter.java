package com.csgo.support;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csgo.condition.Condition;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.exception.code.StandardExceptionCode;
import com.echo.framework.platform.web.request.PageRequest;
import com.echo.framework.platform.web.response.PageResponse;

/**
 * @author admin
 */
public final class DataConverter {

    private DataConverter() {
    }

    public static <T, C extends Condition<T>, R extends PageRequest> C to(Class<C> clazz, R request) {
        try {
            C condition = clazz.newInstance();
            if (null != request) {
                BeanUtils.copyProperties(request, condition);
            }

            Page<T> page = new Page<>(request.getPageIndex(), request.getPageSize());
            condition.setPage(page);
            return condition;
        } catch (Exception e) {
            throw new ApiException(StandardExceptionCode.INTERNAL_ERROR.get(), e.getMessage());
        }
    }

    public static <T, C extends Condition<T>, R extends PageRequest> C to(Class<C> clazz, R request, String sort, String sortBy) {
        try {
            C condition = clazz.newInstance();
            if (null != request) {
                BeanUtils.copyProperties(request, condition);
            }
            if (StringUtils.hasText(sort)) {
                condition.setSort("descending".equals(sort) ? "DESC" : "ASC");
                condition.setSortBy(sortBy);
            }
            Page<T> page = new Page<>(request.getPageIndex(), request.getPageSize());
            condition.setPage(page);
            return condition;
        } catch (Exception e) {
            throw new ApiException(StandardExceptionCode.INTERNAL_ERROR.get(), e.getMessage());
        }
    }

    public static <R, T> PageResponse<R> to(Function<T, R> mapper, Page<T> page) {
        return to(mapper, page, PageResponse<R>::new);
    }

    public static <R, T, Resp extends PageResponse<R>> Resp to(Function<T, R> mapper, Page<T> page, Supplier<Resp> supplier) {
        try {
            Resp pageResponse = supplier.get();
            PageResponse.PageResult<R> pageResult = new PageResponse.PageResult<>();
            for (T record : page.getRecords()) {
                pageResult.getRows().add(mapper.apply(record));
            }
            pageResult.setPageIndex((int) page.getCurrent());
            pageResult.setPageSize((int) page.getSize());
            pageResult.setTotal((int) page.getTotal());
            pageResponse.setData(pageResult);
            return pageResponse;
        } catch (Exception e) {
            throw new ApiException(StandardExceptionCode.INTERNAL_ERROR.get(), e.getMessage());
        }
    }

    public static <T, R> List<R> copy(List<T> sources, Supplier<R> rSupplier) {
        if (CollectionUtils.isEmpty(sources)) {
            return Collections.emptyList();
        }
        return sources.stream().filter(Objects::nonNull)
                .map(copy(rSupplier))
                .collect(Collectors.toList());
    }

    public static <T, R> R copy(T sources, Supplier<R> rSupplier) {
        return copy(rSupplier).apply(sources);
    }

    private static <T, R> Function<T, R> copy(Supplier<R> rSupplier) {
        return t -> {
            R target = rSupplier.get();
            BeanUtils.copyProperties(t, target);
            return target;
        };
    }
}
