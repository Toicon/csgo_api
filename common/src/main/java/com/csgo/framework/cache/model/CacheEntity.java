package com.csgo.framework.cache.model;

import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author admin
 */
@Data
public final class CacheEntity<T> implements Serializable {

    private static final long serialVersionUID = -8828625723718363985L;

    private T data;

    private TimeUnit timeUnit;

    /**
     * 非正整数 代表 不过时
     */
    private long timeout;

    private CacheEntity(T data, long timeout, TimeUnit timeUnit) {
        this.data = data;
        this.timeUnit = timeUnit;
        this.timeout = timeout;
    }

    public static <T> CacheEntity<T> of(T data) {
        return new CacheEntity<>(data, -1, TimeUnit.DAYS);
    }

    public static <T> CacheEntity<T> of(T data, long timeout) {
        return new CacheEntity<>(data, timeout, TimeUnit.DAYS);
    }

    public static <T> CacheEntity<T> of(T data, long timeout, TimeUnit timeUnit) {
        return new CacheEntity<>(data, timeout, timeUnit);
    }

    public boolean isEmpty() {
        return -2 == timeout;
    }

}
