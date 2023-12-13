package com.csgo.redis;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisTemplateFacde {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public String get(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 写入缓存
     */
    public boolean set(final String key, String value) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value);
            result = true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    public boolean setIfAbsent(final String key, String value) {
        boolean result = false;
        try {
            Boolean r = redisTemplate.opsForValue().setIfAbsent(key, value);
            result = r != null && r;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    public void setSimple(String key, String value) {
        BoundValueOperations<String, String> ops = redisTemplate.boundValueOps(key);
        ops.set(value, 5, TimeUnit.MINUTES);
    }

    public String getSimple(String key) {
        BoundValueOperations<String, String> ops = redisTemplate.boundValueOps(key);
        return ops.get();
    }


    public long lPush(String key, String value) {
        try {
            return redisTemplate.opsForList().leftPush(key, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return 0;
    }

    public long leftPushAll(String key, List<String> values) {
        try {
            return redisTemplate.opsForList().leftPushAll(key, values);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return 0;
    }

    public String rPop(String key) {
        try {
            return redisTemplate.opsForList().rightPop(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public List<String> lRange(String key, int start, int end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 写入缓存，设置过期时间
     */
    public boolean set(final String key, String value, long second) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value, second, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 写入缓存，设置过期时间和单位
     */
    public boolean set(final String key, String value, long second, TimeUnit timeUnit) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value, second, timeUnit);
            result = true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 更新缓存
     */
    public String getAndSet(final String key, String value) {
        try {
            return redisTemplate.opsForValue().getAndSet(key, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 删除缓存
     */
    public boolean delete(final String key) {
        log.info("delete redis key:{}", key);
        boolean result = false;
        try {
            redisTemplate.delete(key);
            result = true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * value自减
     */
    public long getAndDecrement(String key) {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        return entityIdCounter.getAndDecrement();
    }

    public boolean tryLock(String key, long second) {
        String lockValue = get(key);
        if (StringUtils.hasText(lockValue)) {
            return false;
        }
        set(key, "1", second);
        return true;
    }

    public void convertAndSend(String channel, String message) {
        try {
            redisTemplate.convertAndSend(channel, message);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
