package com.villa.event.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 事件
 * @作者 微笑い一刀
 * @bbs_url https://blog.csdn.net/u012169821
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Event {
    int value() default 1;//1-默认通过事件方法的参数匹配事件处理器的参数  2-事件方法的返回值匹配事件处理器的参数 3-通过方法名(未实现)
}
