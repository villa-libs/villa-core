package com.villa.util;

import com.villa.config.BodyReaderHttpServletRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class ClassUtil {
    /**
     * 从src对象将属性赋值到desc的属性中
     * @param nullValueNotSet null值是否不覆盖
     */
    public static void copyProperty(Object src,Object desc,boolean nullValueNotSet) {
        Method[] methods = src.getClass().getMethods();
        Class<? extends Object> clz = desc.getClass();
        for (Method method : methods) {
            String methodName = method.getName();
            Class<?> type = method.getReturnType();
            //get开头  并且没有参数
            if (methodName.startsWith("get")&&method.getParameterCount()==0) {
                try {
                    Method mt = clz.getMethod("set"+methodName.substring(3), type);
                    Object value = method.invoke(src);
                    if(nullValueNotSet&&value==null){
                        continue;
                    }
                    mt.invoke(desc,value);
                } catch (Exception e) {
                    //找不到方法不做任何处理
                }
            }
        }
    }

    /**
     * 根据方法名获取方法
     * @param clz   从那个类获取
     * @param name  方法名
     */
    public static Method getMethod(Class clz,String name){
        Method[] methods = clz.getMethods();
        for (Method method : methods) {
            if(method.getName().equals(name)){
                return method;
            }
        }
        return null;
    }

    /**
     * 获取请求对象中的请求参数
     */
    public static String getParamStr(HttpServletRequest request){
        //如果不是封装的请求对象 就代表是上传请求
        if(request instanceof BodyReaderHttpServletRequestWrapper){
            BodyReaderHttpServletRequestWrapper requestWrapper = (BodyReaderHttpServletRequestWrapper)request;
            String paramStr = requestWrapper.getBodyStr();
            //JSON请求
            if(Util.isNotNullOrEmpty(paramStr)){
                return paramStr;
            }
        }
        Map<String, String[]> parameterMap = request.getParameterMap();
        TreeMap<String,String> map = new TreeMap<>();
        parameterMap.keySet().forEach(key->{
            String[] values = parameterMap.get(key);
            map.put(key,values.length==1?values[0]: Arrays.toString(values));
        });
        if(!map.isEmpty()){
            StringBuilder paramStr = new StringBuilder();
            map.keySet().forEach(key->{
                paramStr.append(key+map.get(key));
            });
            return paramStr.toString();
        }
        return null;
    }
    /**
     * 判断当前字节码是否运行在jar包中
     * @return
     */
    public static boolean isRunInJar(Class<?> clz){
        return "jar".equals(clz.getResource("").getProtocol());
    }
}
