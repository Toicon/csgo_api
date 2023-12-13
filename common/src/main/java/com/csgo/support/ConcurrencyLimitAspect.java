package com.csgo.support;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.csgo.redis.RedisTemplateFacde;
import com.echo.framework.platform.exception.ApiException;
import com.echo.framework.platform.exception.code.StandardExceptionCode;
import com.echo.framework.util.Messages;

import lombok.extern.slf4j.Slf4j;

/**
 * @author admin
 */
@Aspect
@Component
@Slf4j
public class ConcurrencyLimitAspect {
    @Autowired
    private RedisTemplateFacde redisTemplateFacde;

    @Pointcut("@annotation(com.csgo.support.ConcurrencyLimit)")
    public void limit() {

    }

    @Around("limit()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object arg = joinPoint.getArgs()[0];
        List<Integer> userIds = arg instanceof List ? (List<Integer>) arg : Collections.singletonList((Integer) arg);
        String tag = String.valueOf(Thread.currentThread().getId());
        try {
            validateAndSet(userIds, tag);
            return joinPoint.proceed();
        } finally {
            userIds.forEach(userId -> {
                String key = getGlobalLockKey(userId);
                if (tag.equals(redisTemplateFacde.get(key))) {
                    redisTemplateFacde.delete(key);
                }
            });
        }
    }

    private void validateAndSet(List<Integer> userIds, String tag) {
        userIds.forEach(userId -> {
            String key = getGlobalLockKey(userId);
            String result = redisTemplateFacde.get(key);
            if (!StringUtils.isEmpty(result)) {
                throw new ApiException(StandardExceptionCode.INTERNAL_ERROR.get(), "不允许重复操作");
            }
            redisTemplateFacde.set(key, tag, 5, TimeUnit.MINUTES);
        });
    }

    private String getGlobalLockKey(int userId) {
        return Messages.format("GLOBAL_OP_LOCK:{}", userId);
    }

}
