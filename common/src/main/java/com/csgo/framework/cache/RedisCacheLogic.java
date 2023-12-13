package com.csgo.framework.cache;

import com.csgo.framework.cache.model.CacheEntity;
import com.echo.framework.support.jackson.json.JSON;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheLogic {

    private final StringRedisTemplate stringRedisTemplate;

    private final Gson gson = new Gson();

    public <Target> Optional<Target> get(String key, Class<Target> clazz) {
        return get(key, clazz, null);
    }

    public <Target> Optional<Target> get(String key, Class<Target> clazz, Supplier<CacheEntity<Target>> supplier) {
        String dataStr = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(dataStr)) {
            if (supplier == null) {
                return Optional.empty();
            }
            CacheEntity<Target> cacheEntity = supplier.get();
            Target t = cacheEntity.getData();
            if (t == null) {
                return Optional.empty();
            }

            if (cacheEntity.getTimeout() < 0) {
                stringRedisTemplate.opsForValue().set(key, JSON.toJSON(t));
            } else {
                stringRedisTemplate.opsForValue().set(key, JSON.toJSON(t), cacheEntity.getTimeout(), cacheEntity.getTimeUnit());
            }
            return Optional.of(t);
        }

        Target entity = JSON.fromJSON(dataStr, clazz);
        return Optional.ofNullable(entity);
    }

    public <Target> void set(String key, Target target, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, JSON.toJSON(target), timeout, unit);
    }

    public <Target> List<Target> getList(String key, Type type, Supplier<CacheEntity<List<Target>>> supplier) {
        String data = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(data)) {
            if (supplier == null) {
                return Lists.newArrayList();
            }
            CacheEntity<List<Target>> cacheEntity = supplier.get();
            List<Target> list = cacheEntity.getData();
            if (cacheEntity.getTimeout() < 0) {
                stringRedisTemplate.opsForValue().set(key, gson.toJson(list, type));
            } else {
                stringRedisTemplate.opsForValue().set(key, gson.toJson(list, type), cacheEntity.getTimeout(), cacheEntity.getTimeUnit());
            }
            return list;
        }

        return gson.fromJson(data, type);
    }

    public void clean(String key) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        stringRedisTemplate.delete(key);
    }

}
