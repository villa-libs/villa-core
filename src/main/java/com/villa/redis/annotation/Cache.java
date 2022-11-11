package com.villa.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * redis缓存
 * 使用此注解 代表此service方法被缓存
 * 缓存key的规则：类名+方法名+参数的json字符串
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {
}
