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
        return getLoginAttrs(request).get(attrName).toString();
    }
    public static Long getAttrByNameToLong(HttpServletRequest request,String attrName){
        return Long.valueOf(getAttrByNameToString(request,attrName));
    }
    public static Integer getAttrByNameToInt(HttpServletRequest request,String attrName){
        return Integer.valueOf(getAttrByNameToString(request,attrName));
    }
    /**
     * 获取登录token
     * @param request 请求对象 要与拦截器配套使用才行
     */
    public static String getToken(HttpServletRequest request){
        return (String) request.getAttribute("v-token");
    }
}
