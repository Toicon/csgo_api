package com.csgo.modular.common.redis.counter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisDailyCounterLogic {

    /**
     * 每日计数器
     */
    private static final String DAILY_COUNTER_KEY_TEMPLATE = "logic:dc:%s:%s:%s";

    private static final String DAILY_PATTERN = "yyyyMMdd";

    private final StringRedisTemplate stringRedisTemplate;

    public Long getCount(RedisCounterType type, Integer userId) {
        try {
            LocalDate now = new LocalDate();

            String key = buildDailyCounterKey(now, type, userId);
            String value = stringRedisTemplate.opsForValue().get(key);
            if (value != null) {
                return Long.parseLong(value);
            }
        } catch (Exception e) {
            log.error("[Get DailyCounterLogic ERROR]", e);
        }
        return 0L;
    }

    public void incrementCount(RedisCounterType type, Integer userId) {
        try {
            LocalDate now = new LocalDate();

            String key = buildDailyCounterKey(now, type, userId);

            stringRedisTemplate.opsForValue().increment(key, 1);
            stringRedisTemplate.expireAt(key, now.plusDays(1).toDate());
        } catch (Exception e) {
            log.error("[Increment DailyCounterLogic ERROR]", e);
        }
    }

    private String buildDailyCounterKey(LocalDate now, RedisCounterType type, Integer userId) {
        String daily = now.toString(DAILY_PATTERN);
        return String.format(DAILY_COUNTER_KEY_TEMPLATE, type.getCode(), daily, userId);
    }

}
