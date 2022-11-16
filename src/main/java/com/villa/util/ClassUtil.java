package com.villa.util;

import com.villa.config.BodyReaderHttpServletRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;

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
    /**
     * 获取所有接口的实现类
     */
    public static List<Class> getAllInterfaceAchieveClass(Class clazz){
        ArrayList<Class> list = new ArrayList<>();
        //判断是否是接口
        if (clazz.isInterface()) {
            try {
                ArrayList<Class> allClass = getAllClassByPath(clazz.getPackage().getName());
                /**
                 * 循环判断路径下的所有类是否实现了指定的接口
                 * 并且排除接口类自己
                 */
                for (int i = 0; i < allClass.size(); i++) {
                    //排除抽象类
                    if(Modifier.isAbstract(allClass.get(i).getModifiers())){
                        continue;
                    }
                    //判断是不是同一个接口
                    if (clazz.isAssignableFrom(allClass.get(i))) {
                        if (!clazz.equals(allClass.get(i))) {
                            list.add(allClass.get(i));
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("出现异常");
            }
        }
        return list;
    }

    /**
     * 从指定路径下获取所有类
     */
    public static ArrayList<Class> getAllClassByPath(String packageName){
        ArrayList<Class> list = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        try {
            ArrayList<File> fileList = new ArrayList<>();
            Enumeration<URL> enumeration = classLoader.getResources(path);
            while (enumeration.hasMoreElements()) {
                URL url = enumeration.nextElement();
                fileList.add(new File(url.getFile()));
            }
            for (int i = 0; i < fileList.size(); i++) {
                list.addAll(findClass(fileList.get(i),packageName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 如果file是文件夹，则递归调用findClass方法，或者文件夹下的类
     * 如果file本身是类文件，则加入list中进行保存，并返回
     * @param file
     * @param packageName
     */
    private static List<Class> findClass(File file, String packageName) {
        List<Class> list = new ArrayList<>();
        if (!file.exists()) {
            return list;
        }
        File[] files = file.listFiles();
        for (File file2 : files) {
            if (file2.isDirectory()) {
                assert !file2.getName().contains(".");//添加断言用于判断
                List<Class> arrayList = findClass(file2, packageName+"."+file2.getName());
                list.addAll(arrayList);
            }else if(file2.getName().endsWith(".class")){
                try {
                    //保存的类文件不需要后缀.class
                    list.add(Class.forName(packageName + '.' + file2.getName().substring(0,
                            file2.getName().length()-6)));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }
}
