package com.villa.util;

/**
 * @作者 微笑い一刀
 * @bbs_url https://blog.csdn.net/u012169821
 */
public class ExceptionUtil {
    /**
     * 获取Throwable里头的错误信息
     */
    public static String getMessage(Throwable e){
        while (e != null) {
            Throwable cause = e.getCause();
            if (cause == null) {
                return e.getMessage();
            }
            e = cause;
        }
        return e.getMessage();
    }
}
