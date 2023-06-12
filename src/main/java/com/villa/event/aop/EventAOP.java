package com.villa.event.aop;

import com.villa.event.annotation.AsyncEventListener;
import com.villa.event.annotation.Event;
import com.villa.event.annotation.EventListener;
import com.villa.event.dto.EventDTO;
import com.villa.event.dto.EventListenerDTO;
import com.villa.util.ClassUtil;
import com.villa.util.SpringContextUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @作者 微笑い一刀
 * @bbs_url https://blog.csdn.net/u012169821
 */
@Component
@Aspect
public class EventAOP {
    // 此方法是否有Event注解
    private static Map<String, EventDTO> hasEventCache = new HashMap<>();
    private static List<EventListenerDTO> eventListenerDTOS = new ArrayList<>();

    /**
     * 切 repository
     */
    @Around("execution(public * com..*.repository..*.*(..))")
    public Object doAfter1(ProceedingJoinPoint point) throws Throwable{
        return handler(point);
    }

    /**
     * 切 service
     */
    @Around("execution(public * com..*.service..*.*(..))")
    public Object doAfter2(ProceedingJoinPoint point) throws Throwable{
        return handler(point);
    }

    private Object handler(ProceedingJoinPoint point) throws Throwable{
        //先执行方法
        Object returnObj = point.proceed();
        //获取当前方法
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        String methodName = method.getDeclaringClass().getName() + "." + method.getName();
        EventDTO eventDTO = hasEventCache.get(methodName);
        //第一次访问
        if (eventDTO == null) {
            Event event = method.getAnnotation(Event.class);
            //此方法没有事件
            if (event == null) {
                hasEventCache.put(methodName, new EventDTO(false));
                return returnObj;
            }
            //有事件
            eventDTO = new EventDTO(true, event.value());
            hasEventCache.put(methodName, eventDTO);
            //缓存了这个方法 但是这个方法没有事件
        } else if (eventDTO != null && !eventDTO.getHasEvent()) return returnObj;
        //事件相关
        initEventListener();
        //参数匹配
        if (eventDTO.getValue() == 1) {
            //触发事件
            Object[] args = point.getArgs();
            eventHandle(args);
            return returnObj;
        }
        //结果匹配
        eventHandle(returnObj);
        return returnObj;
    }

    private void eventHandle(Object... params) throws Exception {
        EventListenerDTO listenerDTO = null;
        for (EventListenerDTO eventListenerDTO : eventListenerDTOS) {
            //先判断参数长度
            Class<?>[] types = eventListenerDTO.getTypes();
            if (types.length != params.length) continue;
            //再判断类型是否一致
            boolean isCurListener = true;
            for (int i = 0; i < types.length; i++) {
                //原本能拿到的是基本数据类型 但是params不会有基本数据类型 所以需要进行转换
                if (!ClassUtil.coverPrimitive2wrap(types[i]).isAssignableFrom(params[i].getClass())) {
                    isCurListener = false;
                }
            }
            if (isCurListener) {
                listenerDTO = eventListenerDTO;
                break;
            }
        }
        if (listenerDTO == null) return;
        if (listenerDTO.isAsync()) {
            //异步执行
            EventListenerDTO finalListenerDTO = listenerDTO;
            new Thread(() -> {
                try {
                    finalListenerDTO.getEventListenerHandler().invoke(finalListenerDTO.getEventListener(), params);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }).start();
            return;
        }
        listenerDTO.getEventListenerHandler().invoke(listenerDTO.getEventListener(), params);
    }

    /**
     * 初始化事件监听器
     */
    private void initEventListener() {
        //已经处理过了
        if (eventListenerDTOS.size() != 0) {
            return;
        }
        initEventListener(SpringContextUtil.getApplicationContext().getBeansWithAnnotation(EventListener.class), false);
        initEventListener(SpringContextUtil.getApplicationContext().getBeansWithAnnotation(AsyncEventListener.class), true);
    }

    private void initEventListener(Map<String, Object> eventListeners, boolean async) {
        eventListeners.keySet().stream().forEach(key -> {
            Object eventListener = eventListeners.get(key);
            Method[] methods = eventListener.getClass().getDeclaredMethods();
            for (Method method : methods) {
                Class<?>[] types = method.getParameterTypes();
                //如果此方法没有参数 则跳过
                if (types.length == 0) {
                    continue;
                }
                EventListenerDTO listenerDTO = new EventListenerDTO();
                listenerDTO.setEventListener(eventListener);
                listenerDTO.setClassName(key);
                listenerDTO.setEventListenerHandler(method);
                listenerDTO.setAsync(async);
                listenerDTO.setTypes(types);
                eventListenerDTOS.add(listenerDTO);
            }
        });
    }
}
