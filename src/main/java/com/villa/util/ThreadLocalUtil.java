package com.villa.util;

/**
 * @作者 微笑い一刀
 * @bbs_url https://blog.csdn.net/u012169821
 */
public class ThreadLocalUtil {
    private static ThreadLocal threadLocal = new ThreadLocal();
    public static void set(Object value){
        threadLocal.set(value);
    }
    public static Object get(){
        return threadLocal.get();
    }
}
