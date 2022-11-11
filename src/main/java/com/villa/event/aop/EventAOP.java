package com.villa.event.aop;

import com.villa.event.annotation.AsyncEventListener;
import com.villa.event.annotation.Event;
import com.villa.event.annotation.EventListener;
import com.villa.event.dto.EventDTO;
import com.villa.event.dto.EventListenerDTO;
import com.villa.util.SpringContextUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.villa.util.ExceptionUtil.getMessage;

/**
 * @作者 微笑い一刀
 * @bbs_url https://blog.csdn.net/u012169821
 */
@Component
@Aspect
public class EventAOP {
    // 此方法是否有Event注解
    private static Map<String,EventDTO> hasEventCache = new HashMap<>();
    private static Map<String, EventListenerDTO> eventListenerDTOs = new HashMap<>();
    /**
     * 切 repository
     */
    @Around("execution(public * com..*.repository..*.*(..))")
    public Object doAfter1(ProceedingJoinPoint point){
        return handler(point);
    }
    /**
     * 切 service
     */
    @Around("execution(public * com..*.service..*.*(..))")
    public Object doAfter2(ProceedingJoinPoint point){
        return handler(point);
    }
    private Object handler(ProceedingJoinPoint point){
        try {
            Object returnObj = point.proceed();
            //获取当前方法
            Method method = ((MethodSignature) point.getSignature()).getMethod();
            String serviceName = method.getDeclaringClass().getName()+"."+method.getName();
            EventDTO eventDTO = hasEventCache.get(serviceName);
            //第一次访问
            if(eventDTO==null){
                Event event = method.getAnnotation(Event.class);
                //此方法没有事件
                if(event == null){
                    hasEventCache.put(serviceName,new EventDTO(false));
                    return returnObj;
                }
                eventDTO = new EventDTO(true,event.value());
                hasEventCache.put(serviceName,eventDTO);
                //缓存了这个方法 但是这个方法没有事件
            }else if(eventDTO!=null&&!eventDTO.getHasEvent())return returnObj;

            //事件相关
            initEventListener();
            if(eventDTO.getValue()==1){
                //触发事件
                Object[] args = point.getArgs();
                StringBuilder sb = new StringBuilder();
                for (Object arg : args) {
                    sb.append(arg.getClass().getName());
                }
                eventHandle(sb.toString(),args);
                return returnObj;
            }
            //获取返回值类型
            eventHandle(returnObj.getClass().getName(),returnObj);
            return returnObj;
        }catch (Throwable throwable){
            throw new RuntimeException(getMessage(throwable));
        }
    }
    private void eventHandle(String clz,Object...params) throws Exception {
        EventListenerDTO listenerDTO = eventListenerDTOs.get(clz);
        if(listenerDTO==null)return;
        if(listenerDTO.isAsync()){
            //异步执行
            new Thread(()-> {
                try {
                    listenerDTO.getEventListenerHandler().invoke(listenerDTO.getEventListener(),params);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }).start();
            return;
        }
        listenerDTO.getEventListenerHandler().invoke(listenerDTO.getEventListener(),params);
    }
    /**
     * 初始化事件监听器
     */
    private void initEventListener(){
        if(eventListenerDTOs.size()!=0){
            return;
        }
        initEventListener( SpringContextUtil.getApplicationContext().getBeansWithAnnotation(EventListener.class),false);
        initEventListener( SpringContextUtil.getApplicationContext().getBeansWithAnnotation(AsyncEventListener.class),true);
    }

    private void initEventListener(Map<String, Object> eventListeners,boolean async){
        eventListeners.keySet().stream().forEach(key->{
            Object eventListener = eventListeners.get(key);
            Method[] methods = eventListener.getClass().getDeclaredMethods();
            for (Method method : methods) {
                Class<?>[] types = method.getParameterTypes();
                if(types.length == 0){
                    continue;
                }
                StringBuilder sb = new StringBuilder();
                for (Class<?> type : types) {
                    sb.append(type.getName());
                }
                EventListenerDTO listenerDTO = new EventListenerDTO();
                listenerDTO.setEventListener(eventListener);
                listenerDTO.setClassName(key);
                listenerDTO.setEventListenerHandler(method);
                listenerDTO.setAsync(async);
                eventListenerDTOs.put(sb.toString(),listenerDTO);
            }
        });
    }
}
