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
        Object attr = getLoginAttrs(request).get(attrName);
        return attr==null?null:attr.toString();
    }
    public static Long getAttrByNameToLong(HttpServletRequest request,String attrName){
        String attr = getAttrByNameToString(request, attrName);
        return Util.isNullOrEmpty(attr)?null:Long.valueOf(attr);
    }
    public static Integer getAttrByNameToInt(HttpServletRequest request,String attrName){
        String attr = getAttrByNameToString(request, attrName);
        return Util.isNullOrEmpty(attr)?null:Integer.valueOf(attr);
    }
    /**
     * 获取登录token
     * @param request 请求对象 要与拦截器配套使用才行
     */
    public static String getToken(HttpServletRequest request){
        return (String) request.getAttribute("v-token");
    }
}
