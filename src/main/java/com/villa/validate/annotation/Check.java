package com.villa.validate.annotation;

import com.villa.util.Util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Check {
    /**
     * 要验证的属性
     * 会遍历方法的参数对象去寻找
     * 注意：如果service方法有多个参数 而且有相同名字的属性,这里会默认取第一个
     */
    String[] field();
    /**
     * 验证方法 默认验证非空 可以自定义,如果自定义,那么clz也需要传入自定义的类字节码
     */
    String type() default Util.assertionIsNotNullOrEmpty;

    Class clz() default Util.class;//校验方法的所在类 可以自定义
    /**
     * 验证失败的消息
     */
    String msg() default "";//错误提示消息 默认空 如果自定义验证规则的话 就不需要了 直接在验证方法中抛错就行
}
