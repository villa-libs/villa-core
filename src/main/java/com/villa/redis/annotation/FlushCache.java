package com.villa.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * service方法拥有此注解 代表需要清除某些缓存
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FlushCache {
    Point[] value();//当前方法需要刷新的缓存
}
