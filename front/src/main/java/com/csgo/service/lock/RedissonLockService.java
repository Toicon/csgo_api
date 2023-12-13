package com.csgo.service.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.lang.management.LockInfo;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedissonLockService {

    private final RedissonClient redissonClient;

    public RLock acquire(String lockKey, long expire, TimeUnit unit) {
        try {
            final RLock lockInstance = redissonClient.getLock(lockKey);
            final boolean locked = lockInstance.tryLock(expire, unit);
            return locked ? lockInstance : null;
        } catch (InterruptedException e) {
            return null;
        }
    }

    public boolean releaseLock(String lockKey, RLock lockInstance) {
        if (lockInstance == null) {
            return false;
        }
        if (lockInstance.isHeldByCurrentThread()) {
            try {
                lockInstance.unlockAsync().get();
                return true;
            } catch (ExecutionException | InterruptedException e) {
                log.error("releaseLock fail,lockKey={}", lockKey);
                return false;
            }
        }
        return false;
    }

}
