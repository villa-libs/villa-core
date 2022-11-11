package com.villa.util;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class SystemUtil {
    /**
     * 获取登录者用户对象
     * @param request   请求对象 要与拦截器配套使用才行
     */
    public static Map<String,Object> getLoginAttrs(HttpServletRequest request){
        Map<String, Object> loginAttrs = (Map<String, Object>) request.getAttribute("loginAttrs");
        return loginAttrs==null?new HashMap<>():loginAttrs;
    }
    public static String getAttrByNameToString(HttpServletRequest request,String attrName){
        return (String)getLoginAttrs(request).get(attrName);
    }
    public static Long getAttrByNameToLong(HttpServletRequest request,String attrName){
        return (Long)getLoginAttrs(request).get(attrName);
    }
    /**
     * 获取登录token
     * @param request 请求对象 要与拦截器配套使用才行
     */
    public static String getToken(HttpServletRequest request){
        return (String) request.getAttribute("v-token");
    }
}
