package com.villa.event.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @作者 微笑い一刀
 * @bbs_url https://blog.csdn.net/u012169821
 * 此事件监听器异步执行
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsyncEventListener {
}
