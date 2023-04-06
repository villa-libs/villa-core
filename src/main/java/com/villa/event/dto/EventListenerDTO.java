package com.villa.event.dto;

import java.lang.reflect.Method;

/**
 * @作者 微笑い一刀
 * @bbs_url https://blog.csdn.net/u012169821
 */
public class EventListenerDTO {
    //事件监听器对象
    private Object EventListener;
    //事件监听器对象的全限定名
    private String className;
    //事件监听器中的处理方法
    private Method eventListenerHandler;
    //是否异步 默认false
    private boolean async;
    //当前方法的参数类型列表
    private Class<?>[] types;

    public Class<?>[] getTypes() {
        return types;
    }

    public void setTypes(Class<?>[] types) {
        this.types = types;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public Object getEventListener() {
        return EventListener;
    }

    public void setEventListener(Object eventListener) {
        EventListener = eventListener;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Method getEventListenerHandler() {
        return eventListenerHandler;
    }

    public void setEventListenerHandler(Method eventListenerHandler) {
        this.eventListenerHandler = eventListenerHandler;
    }
}
