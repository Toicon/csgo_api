package com.csgo.web.support;

import com.aliyun.openservices.shade.com.alibaba.fastjson.JSON;
import com.csgo.domain.plus.log.OperationLog;
import com.csgo.service.opeartion.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 2021/5/30
 */


@Aspect
@Component
@Slf4j
public class OperationLogAspect {
    @Autowired
    private SiteContext siteContext;
    @Autowired
    private OperationLogService logService;


    @Pointcut("@annotation(com.csgo.web.support.Log)")
    public void operationLogPointCut() {
    }

    @AfterReturning(value = "operationLogPointCut()", returning = "keys")
    public void save(JoinPoint joinPoint, Object keys) {
        // 获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 从获取RequestAttributes中获取HttpServletRequest的信息
        assert requestAttributes != null;
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        try {
            OperationLog log = new OperationLog();
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 获取切入点所在的方法
            Method method = signature.getMethod();
            // 获取操作
            Log opLog = method.getAnnotation(Log.class);
            if (opLog != null) {
                log.setDescription(opLog.desc()); // 操作描述
            }
            // 请求的参数
            Map<String, String> rtnMap = convertMap(request.getParameterMap());
            log.setParams(JSON.toJSONString(rtnMap)); // 请求参数
            log.setResponses(JSON.toJSONString(keys)); // 返回结果
            UserInfo userInfo = siteContext.getCurrentUser();
            if (null != userInfo) {
                log.setUserId(userInfo.getId()); // 请求用户ID
                log.setUserName(siteContext.getCurrentUser().getName()); // 请求用户名称
            }
            log.setUri(request.getRequestURI()); // 请求URI
            logService.insert(log);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private Map<String, String> convertMap(Map<String, String[]> paramMap) {
        Map<String, String> rtnMap = new HashMap<>();
        for (String key : paramMap.keySet()) {
            rtnMap.put(key, paramMap.get(key)[0]);
        }
        return rtnMap;
    }
}
