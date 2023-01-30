package com.villa.redis.aop;

import com.villa.redis.annotation.FlushCache;
import com.villa.redis.annotation.Point;
import com.villa.redis.RedisClient;
import com.villa.util.Util;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.lang.model.type.NullType;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.villa.util.ExceptionUtil.getMessage;

@Component
@Aspect
public class FlushCacheAOP {
    @Autowired(required = false)
    private RedisClient redisClient;
    //flushCache注解的缓存
    private static Map<Method,Boolean> serviceMethodHasFlushCache = new HashMap<>();
    private static Map<Method, Point[]> pointCache = new HashMap<>();
    private static Map<Class,String> clzCache = new HashMap<>();
    /**
     * 清除缓存
     */
    @After("execution(public * com..*.service..*.*(..))")
    public void doAfter(JoinPoint point) {
        try{
            if(Util.isNull(redisClient)){
                return;
            }
            Method method = ((MethodSignature) point.getSignature()).getMethod();
            //是否存在cache注解
            Boolean hasFlushCache = serviceMethodHasFlushCache.get(method);
            if(hasFlushCache!=null&&!hasFlushCache){//不是第一次执行 没有FlushCache注解 直接放行
                return;
            }

            if(hasFlushCache==null){//第一次执行此方法 需要记录一些消息 后面不执行
                FlushCache flushCache = method.getAnnotation(FlushCache.class);
                if(flushCache==null){//没有这个注解
                    serviceMethodHasFlushCache.put(method,false);
                    return;
                }
                //这里代表有这个注解
                serviceMethodHasFlushCache.put(method,true);
                //有值 就把point存起来
                pointCache.put(method,flushCache.value());
            }
            //当前Service的类名
            String className = method.getDeclaringClass().getName();
            //从缓存中取 注解集
            Point[] points = pointCache.get(method);
            for (Point p : points) {
                //此point 记录了clz
                if(p.clz() != NullType.class){
                    //从缓存找
                    className = clzCache.get(p.clz());
                    //没找到
                    if(Util.isNullOrEmpty(className)){
                        className = p.clz().getName();
                        clzCache.put(p.clz(),className);
                    }
                }
                String redisKey = "cache_"+className+"."+p.value()+"_*";
                Set<String> keys = redisClient.keys(redisKey);
                for (String key : keys) {
                    redisClient.del(key);
                }
            }
        }catch (Throwable throwable){
            throw new RuntimeException(getMessage(throwable));
        }
    }
}
