package com.villa.validate.aop;

import com.villa.util.ClassUtil;
import com.villa.util.Util;
import com.villa.validate.annotation.Check;
import com.villa.validate.annotation.Validate;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.villa.util.ExceptionUtil.getMessage;

@Component
@Aspect
public class ValidateAOP {
    private static Map<String, Method> validateMethodCache = new HashMap<>();//验证方法的缓存
    private static Map<String, Boolean> serviceMethodHasValidate = new HashMap<>();
    private static Map<String, Check[]> checkCache = new HashMap<>();

    /**
     * 所有service方法
     */
    @Before("execution(public * com..*.service..*.*(..))")
    public void doBefore(JoinPoint point) {
        try{
            Method method = ((MethodSignature) point.getSignature()).getMethod();
            //service缓存
            String serviceName = method.getDeclaringClass().getName() + "." + method.getName();
            Boolean hasValidate = serviceMethodHasValidate.get(serviceName);
            if (hasValidate == null) {
                Validate validate = method.getAnnotation(Validate.class);
                if (validate == null) {
                    serviceMethodHasValidate.put(serviceName, false);
                    return;
                }
                //这里代表需要验证
                serviceMethodHasValidate.put(serviceName, true);
                checkCache.put(serviceName, validate.value());
            } else if (hasValidate != null && !hasValidate) return;

            //从缓存中取 注解集
            Check[] checks = checkCache.get(serviceName);
            for (Check check : checks) {
                String[] fields = check.field();
                String type = check.type();
                String msg = check.msg();
                Class clz = check.clz();
                //先从缓存取验证方法,看是否存在 不存在再去反射拿  反射比较消耗性能
                Method utilMethod = validateMethodCache.get(type);
                if (utilMethod == null) {
                    //获取验证方法
                    utilMethod = ClassUtil.getMethod(clz, type);
                    Util.assertionIsNotNull(utilMethod, clz + type + "找不到,请确认方法名是否正确");
                    validateMethodCache.put(type, utilMethod);
                }
                //从参数中获取字段值
                List<Object> params = new ArrayList<>();
                for (String field : fields) {
                    Object[] args = point.getArgs();
                    for (Object arg : args) {
                        try {
                            //反射调用get方法
                            PropertyDescriptor descriptor = new PropertyDescriptor(field, arg.getClass());
                            Object value = descriptor.getReadMethod().invoke(arg);
                            params.add(value);
                        } catch (Exception e) {
                        }
                    }
                }
                params.add(msg);
                //方法存在可变参数,反射时必传 否则会报参数个数对不上问题
                params.add(new Object[]{});
                //执行验证方法
                utilMethod.invoke(null, params.toArray());
            }
        }catch (Throwable throwable){
            throw new RuntimeException(getMessage(throwable));
        }
    }
}
