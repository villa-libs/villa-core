package com.villa.redis.aop;

import com.alibaba.fastjson.JSON;
import com.villa.redis.RedisClient;
import com.villa.redis.annotation.Cache;
import com.villa.redis.aop.dto.CacheMethodReturnDTO;
import com.villa.util.Util;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Aspect
public class CacheAOP {
    @Autowired(required = false)
    private RedisClient redisClient;
    //cache注解的缓存
    private static Map<String, Boolean> serviceMethodHasCache = new HashMap<>();
    private static Map<String, CacheMethodReturnDTO> serviceMethodReturnCache = new HashMap<>();

    /**
     * 存取缓存
     */
    @Around("execution(public * com..*.service..*.*(..))")
    public Object Around1(ProceedingJoinPoint point) throws Throwable {
        return handler(point);
    }

    @Around("execution(public * com..*.repository..*.*(..))")
    public Object Around2(ProceedingJoinPoint point) throws Throwable {
        return handler(point);
    }

    public Object handler(ProceedingJoinPoint point) throws Throwable {
        if (Util.isNull(redisClient)) {
            return point.proceed();
        }
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        //service名称
        String serviceName = method.getDeclaringClass().getName() + "." + method.getName();
        //是否存在cache注解
        Boolean hasValidate = serviceMethodHasCache.get(serviceName);
        if (hasValidate == null) {//==null 代表还未执行过这个service方法
            Cache cache = method.getAnnotation(Cache.class);
            if (cache == null) {//没有这个注解
                //记录这个方法不需要缓存
                serviceMethodHasCache.put(serviceName, false);
                return point.proceed();
            }
            //这里代表有这个注解
            serviceMethodHasCache.put(serviceName, true);
            CacheMethodReturnDTO returnDTO = new CacheMethodReturnDTO();
            if (Collection.class.isAssignableFrom(method.getReturnType())) {
                returnDTO.setArray(true);
                returnDTO.setClz((Class) ((ParameterizedType) ((MethodSignature) point.getSignature()).getMethod().getGenericReturnType()).getActualTypeArguments()[0]);
            } else {
                returnDTO.setClz(method.getReturnType());
            }
            serviceMethodReturnCache.put(serviceName, returnDTO);
            //不是第一次执行 也没有Cache注解 直接放行
        } else if (hasValidate != null && !hasValidate) return point.proceed();
        //这里需要进行数据缓存 service方法的全限定名+参数
        String redisKey = "cache_" + serviceName + "_" + Arrays.toString(point.getArgs());
        //判断缓存中是否存在值
        String result = redisClient.get(redisKey);
        if (Util.isNotNullOrEmpty(result)) {
            //存在就直接返回
            CacheMethodReturnDTO returnDTO = serviceMethodReturnCache.get(serviceName);
            if (returnDTO.isArray()) {
                return JSON.parseArray(result, returnDTO.getClz());
            } else {
                return JSON.parseObject(result, returnDTO.getClz());
            }
        }
        Object resultObj = point.proceed();
        //不存在就存 数据默认保存7天
        redisClient.set(redisKey, JSON.toJSONString(resultObj), 60 * 60 * 24 * 7);
        return resultObj;
    }
}
