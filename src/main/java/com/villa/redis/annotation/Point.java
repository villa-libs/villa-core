package com.villa.redis.annotation;

import javax.lang.model.type.NullType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存点
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Point {
    Class clz() default NullType.class;//那个service类 不填默认就是本类
    String value();//被缓存的方法名
}
