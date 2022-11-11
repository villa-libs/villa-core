package com.villa.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 加上此注解的方法 方法不需要签名
 */
@Target(ElementType.METHOD)//参数对方法有效
@Retention(RetentionPolicy.RUNTIME)//运行时有效
public @interface NoSign {
}
