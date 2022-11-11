package com.villa.util;

import com.alibaba.fastjson.JSON;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class HttpUtil {
    private static String charSet = "UTF-8";
    public static String sendPostJSON(String url, Map<String, Object> parameters){
        return sendPost(url,null,parameters,true);
    }
    /**
     * 发送json格式的post方法
     * @param url           目标url
     * @param headers       请求头
     * @param parameters    参数
     * @return              返回结果
     */
    public static String sendPostJSON(String url, Map<String,String> headers, Map<String, Object> parameters){
        return sendPost(url,headers,parameters,true);
    }
    /**
     * 发送formdata格式的post方法
     */
    public static String sendPost(String url, Map<String, Object> parameters){
        return sendPost(url,null,parameters,false);
    }
    /**
     * 发送formdata格式的post方法
     */
    public static String sendPost(String url, Map<String,String> headers, Map<String, Object> parameters){
        return sendPost(url,headers,parameters,false);
    }

    /**
     * 发送参数加密格式的post
     * @param url       请求目标
     * @param headers   请求头
     * @param jsonStr   参数体json字符串
     * @return
     */
    public static String sendPostJSONStr(String url, Map<String,String> headers, String jsonStr){
        DataOutputStream dos = null;
        HttpURLConnection conn = null;
        try{
            conn = connect(url,"POST");
            setHeaders(headers,conn,true);
            dos = setParams(conn,jsonStr);
            return handlerResult(conn);
        }catch (Exception e){
            e.printStackTrace();
            close(dos,conn);
        }
        return null;
    }
    public static String sendPost(String url, Map<String,String> headers, Map<String, Object> parameters,boolean isJson){
        DataOutputStream dos = null;
        HttpURLConnection conn = null;
        try{
            conn = connect(url,"POST");
            setHeaders(headers,conn,isJson);
            dos = setParams(conn,parameters,isJson);
            return handlerResult(conn);
        }catch (Exception e){
            e.printStackTrace();
            close(dos,conn);
        }
        return null;
    }
    public static String sendGet(String url){
        HttpURLConnection conn = null;
        try{
            conn = connect(url,"GET");
            return handlerResult(conn);
        }catch (Exception e){
            e.printStackTrace();
            close(null,conn);
        }
        return null;
    }
    public static String sendGet(String url, Map<String,String> headers){
        HttpURLConnection conn = null;
        try{
            conn = connect(url,"GET");
            //设置请求头
            setHeaders(headers,conn,false);
            return handlerResult(conn);
        }catch (Exception e){
            e.printStackTrace();
            close(null,conn);
        }
        return null;
    }
    public static String uploadFile(String url, Map<String,String> headers,Map<String, Object> parameters,String fileName,byte[] fileData) {
        DataOutputStream dos = null;
        HttpURLConnection conn = null;
        try {
            // 换行符
            final String newLine = "\r\n";
            final String boundaryPrefix = "--";
            // 定义数据分隔线
            String BOUNDARY = "---------7d4a6d158c9";
            conn = connect(url, "POST");
            setHeaders(headers,conn,false);
            //设置请求格式
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            // 上传文件
            StringBuilder sb = new StringBuilder();
            //有额外参数
            if(parameters!=null){
                for (String paramKey : parameters.keySet()) {
                    sb.append(boundaryPrefix);
                    sb.append(BOUNDARY);
                    sb.append(newLine);
                    sb.append("Content-Disposition: form-data;name=\""+paramKey+"\"");
                    sb.append(newLine);
                    sb.append(newLine);
                    sb.append(parameters.get(paramKey));
                    sb.append(newLine);
                }
            }

            sb.append(boundaryPrefix);
            sb.append(BOUNDARY);
            sb.append(newLine);
            // 文件参数
            sb.append("Content-Disposition: form-data;name=\"file\"; filename=\"" + fileName + "\"");
            sb.append(newLine);
            sb.append("Content-Type:application/octet-stream");
            // 参数头设置完以后需要两个换行，然后才是参数内容
            sb.append(newLine);
            sb.append(newLine);

            dos = new DataOutputStream(conn.getOutputStream());
            dos.write(sb.toString().getBytes());
            dos.write(fileData);
            // 最后添加换行
            dos.write(newLine.getBytes());
            // 写上结尾标识
            dos.write((newLine + boundaryPrefix + BOUNDARY + boundaryPrefix + newLine).getBytes());
            dos.flush();
            return handlerResult(conn);
        } catch (Exception e) {
            e.printStackTrace();
            close(dos,conn);
        }
        return null;
    }


    /**
     * 获取链接对象
     */
    private static HttpURLConnection connect(String url,String requestMethod) throws IOException {
        URL restURL = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) restURL.openConnection();
        //超时时间设置为1小时
        conn.setConnectTimeout(1000*60*60);
        conn.setReadTimeout(1000*60*60);
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("Charsert", charSet);
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        conn.setRequestMethod(requestMethod);
        conn.setDoOutput(true);
        conn.setAllowUserInteraction(false);
        return conn;
    }
    /**
     * 参数类型转换
     */
    private static String paramsConvert(Map<String, Object> parameters) throws UnsupportedEncodingException {
        if(parameters==null||parameters.size()==0){
            return "";
        }
        StringBuffer sb = new StringBuffer();// 处理请求参数
        // 编码请求参数
        for (String name : parameters.keySet()) {
            Object object = parameters.get(name);
            if (object instanceof Integer||object instanceof Long) {
                sb.append(name).append("=").append(java.net.URLEncoder.encode((object.toString()), charSet));
            } else if (object instanceof Date) {
                Calendar cal = Calendar.getInstance();
                cal.setTime((Date) object);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sb.append(name).append("=").append(java.net.URLEncoder.encode(sdf.format(cal.getTime()), charSet));
            }else if(object instanceof String[]){
                sb.append(name).append("=").append(Arrays.toString((String[])object));
            }else {
                if (object.getClass().isArray()) {//判断是否是字符串数组
                    String[] values = (String[]) object;
                    for (String va : values) {
                        sb.append(name).append("=").append(java.net.URLEncoder.encode((String) va, charSet));
                    }
                } else {
                    sb.append(name).append("=").append(java.net.URLEncoder.encode((String) parameters.get(name), charSet));
                }
            }
            sb.append("&");
        }
        return sb.toString().substring(0, sb.toString().length() - 1);
    }

    /**
     * 处理返回数据
     */
    private static String handlerResult(HttpURLConnection conn) throws IOException {
        BufferedReader bReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), charSet));
        String line, resultStr = "";
        while (null != (line = bReader.readLine())) {
            resultStr += line;
        }
        bReader.close();
        return resultStr;
    }
    /**
     * 设置请求头
     */
    private static void setHeaders(Map<String,String> headers,HttpURLConnection conn,boolean isJson){
        if (headers!=null&&headers.size()>0) {
            Set<String> keys = headers.keySet();
            for (String key : keys) {
                conn.setRequestProperty(key, headers.get(key)+"");
            }
        }
        if(isJson){
            conn.setRequestProperty("Content-Type", "application/json");
        }
    }
    /**
     * 设置参数 get方法是没有参数设置的
     */
    private static DataOutputStream setParams(HttpURLConnection conn,String jsonStr) throws IOException {
        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
        dos.write(jsonStr.getBytes(charSet));
        dos.flush();
        return dos;
    }
    /**
     * 设置参数 get方法是没有参数设置的
     */
    private static DataOutputStream setParams(HttpURLConnection conn,Map<String, Object> parameters,boolean isJson) throws IOException {
        String params;
        if(isJson){
            conn.setRequestProperty("Content-Type", "application/json");
            params = JSON.toJSONString(parameters);
        }else{
            params = paramsConvert(parameters);
        }
        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
        dos.write(params.getBytes(charSet)); //解决中文乱码
        dos.flush();
        return dos;
    }
    /**
     * 关闭资源
     */
    private static void close(DataOutputStream dos,HttpURLConnection conn){
        if(dos!=null){
            try {
                dos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if(conn!=null){
            try{
                conn.disconnect();
            }catch (Exception e2){}
        }
    }
}
