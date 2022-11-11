package com.villa.auth.annotation;

import java.lang.annotation.*;

/**
 * 加上此注解的方法 不会被登录拦截
 */
@Target(ElementType.METHOD)//参数对方法有效
@Retention(RetentionPolicy.RUNTIME)//运行时有效
public @interface NoLogin {
}
