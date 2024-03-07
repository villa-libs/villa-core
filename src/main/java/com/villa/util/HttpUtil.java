package com.villa.util;

import com.alibaba.fastjson.JSON;
import com.villa.dto.ProxyDTO;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class HttpUtil {
    private static String charSet = "UTF-8";

    public static String sendPostJSONStr(String url, String jsonStr) throws IOException{
        return sendPostJSONStr(url, null, jsonStr, null);
    }

    public static String sendPostJSONStr(String url, Map<String, String> headers, String jsonStr) throws IOException{
        return sendPostJSONStr(url, headers, jsonStr, null);
    }

    /**
     * 发送参数加密格式的post
     *
     * @param url     请求目标
     * @param headers 请求头
     * @param jsonStr 参数体json字符串
     * @return
     */
    public static String sendPostJSONStr(String url, Map<String, String> headers, String jsonStr, ProxyDTO proxyDTO) throws IOException{
        DataOutputStream dos = null;
        HttpURLConnection conn = null;
        try {
            conn = connect(url, "POST", proxyDTO);
            setHeaders(headers, conn, true);
            dos = setParams(conn, jsonStr);
            return handlerResult(conn);
        } catch (Exception e) {
            e.printStackTrace();
            close(dos, conn);
        }
        return null;
    }

    public static String sendPostJSON(String url, Map<String, Object> parameters) throws IOException{
        return sendPost(url, null, parameters, true, null);
    }

    /**
     * 发送json格式的post方法
     *
     * @param url        目标url
     * @param headers    请求头
     * @param parameters 参数
     * @return 返回结果
     */
    public static String sendPostJSON(String url, Map<String, String> headers, Map<String, Object> parameters)throws IOException {
        return sendPost(url, headers, parameters, true, null);
    }

    /**
     * 发送formdata格式的post方法
     */
    public static String sendPost(String url, Map<String, Object> parameters) throws IOException{
        return sendPost(url, null, parameters, false, null);
    }

    /**
     * 发送formdata格式的post方法
     */
    public static String sendPost(String url, Map<String, String> headers, Map<String, Object> parameters, ProxyDTO proxyDTO) throws IOException{
        return sendPost(url, headers, parameters, false, proxyDTO);
    }

    public static String sendPost(String url, Map<String, String> headers, Map<String, Object> parameters) throws IOException{
        return sendPost(url, headers, parameters, false, null);
    }

    public static String sendPost(String url, Map<String, String> headers, Map<String, Object> parameters, boolean isJson) throws IOException{
        return sendPost(url, headers, parameters, isJson, null);
    }

    public static String sendPost(String url, Map<String, String> headers, Map<String, Object> parameters, boolean isJson, ProxyDTO proxyDTO) throws IOException{
        DataOutputStream dos = null;
        HttpURLConnection conn = null;
        try {
            conn = connect(url, "POST", proxyDTO);
            setHeaders(headers, conn, isJson);
            dos = setParams(conn, parameters, isJson);
            return handlerResult(conn);
        } finally {
            close(null, conn);
        }
    }

    public static String sendGet(String url)throws IOException {
        return sendGet(url, null, null);
    }

    public static String sendGet(String url, Map<String, String> headers)throws IOException {
        return sendGet(url, headers, null);
    }

    public static String sendGet(String url, ProxyDTO proxyDTO)throws IOException {
        return sendGet(url, null, proxyDTO);
    }

    public static String sendGet(String url, Map<String, String> headers, ProxyDTO proxyDTO) throws IOException{
        HttpURLConnection conn = null;
        try {
            conn = connect(url, "GET", proxyDTO);
            //设置请求头
            setHeaders(headers, conn, false);
            return handlerResult(conn);
        } finally {
            close(null, conn);
        }
    }

    public static String uploadFile(String url, String fileName, byte[] fileData) {
        return uploadFile(url, null, null, fileName, fileData, null);
    }

    public static String uploadFile(String url, Map<String, Object> parameters, String fileName, byte[] fileData) {
        return uploadFile(url, null, parameters, fileName, fileData, null);
    }

    public static String uploadFile(String url, Map<String, String> headers, Map<String, Object> parameters, String fileName, byte[] fileData) {
        return uploadFile(url, headers, parameters, fileName, fileData, null);
    }

    public static String uploadFile(String url, Map<String, String> headers, Map<String, Object> parameters, String fileName, byte[] fileData, ProxyDTO proxyDTO) {
        DataOutputStream dos = null;
        HttpURLConnection conn = null;
        try {
            // 换行符
            final String newLine = "\r\n";
            final String boundaryPrefix = "--";
            // 定义数据分隔线
            String BOUNDARY = "---------7d4a6d158c9";
            conn = connect(url, "POST", proxyDTO);
            setHeaders(headers, conn, false);
            //设置请求格式
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            // 上传文件
            StringBuilder sb = new StringBuilder();
            //有额外参数
            if (parameters != null) {
                for (String paramKey : parameters.keySet()) {
                    sb.append(boundaryPrefix);
                    sb.append(BOUNDARY);
                    sb.append(newLine);
                    sb.append("Content-Disposition: form-data;name=\"" + paramKey + "\"");
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
            close(dos, conn);
        }
        return null;
    }


    /**
     * 获取链接对象
     */
    private static HttpURLConnection connect(String url, String requestMethod, ProxyDTO proxyDTO) throws IOException {
        URL restURL = new URL(url);
        HttpURLConnection conn;
        if (proxyDTO == null) {
            conn = (HttpURLConnection) restURL.openConnection();
        } else {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyDTO.getIp(), proxyDTO.getPort()));
            conn = (HttpURLConnection) restURL.openConnection(proxy);
            conn.setRequestProperty("Proxy-Authorization",
                    "Basic "+Base64.getEncoder().encodeToString((proxyDTO.getUsername()+":"+proxyDTO.getPassword()).getBytes()));
        }
        //超时时间设置为1小时
        conn.setConnectTimeout(1000 * 60 * 60);
        conn.setReadTimeout(1000 * 60 * 60);
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("Charsert", charSet);
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        conn.setRequestMethod(requestMethod);
        conn.setDoOutput(true);
        conn.setAllowUserInteraction(true);
        return conn;
    }

    /**
     * 参数类型转换
     */
    private static String paramsConvert(Map<String, Object> parameters) throws UnsupportedEncodingException {
        if (parameters == null || parameters.size() == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer();// 处理请求参数
        // 编码请求参数
        for (String name : parameters.keySet()) {
            Object object = parameters.get(name);
            if (object instanceof Integer || object instanceof Long) {
                sb.append(name).append("=").append(java.net.URLEncoder.encode((object.toString()), charSet));
            } else if (object instanceof Date) {
                Calendar cal = Calendar.getInstance();
                cal.setTime((Date) object);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sb.append(name).append("=").append(java.net.URLEncoder.encode(sdf.format(cal.getTime()), charSet));
            } else if (object instanceof String[]) {
                sb.append(name).append("=").append(Arrays.toString((String[]) object));
            } else {
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
        // 先得到返回状态码
        int code = conn.getResponseCode();
        InputStream inputStream;
        if (code == 200) {
            inputStream = conn.getInputStream();
        }else if(code == 204){//响应成功  但是无内容
            return null;
        }else {
            inputStream = conn.getErrorStream();
        }
        if(inputStream == null){
            return null;
        }
        BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, charSet));
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
    private static void setHeaders(Map<String, String> headers, HttpURLConnection conn, boolean isJson) {
        if (headers != null && headers.size() > 0) {
            Set<String> keys = headers.keySet();
            for (String key : keys) {
                conn.setRequestProperty(key, headers.get(key) + "");
            }
        }
        if (isJson) {
            conn.setRequestProperty("Content-Type", "application/json");
        }
    }

    /**
     * 设置参数 get方法是没有参数设置的
     */
    private static DataOutputStream setParams(HttpURLConnection conn, String jsonStr) throws IOException {
        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
        dos.write(jsonStr.getBytes(charSet));
        dos.flush();
        return dos;
    }

    /**
     * 设置参数 get方法是没有参数设置的
     */
    private static DataOutputStream setParams(HttpURLConnection conn, Map<String, Object> parameters, boolean isJson) throws IOException {
        String params;
        if (isJson) {
            conn.setRequestProperty("Content-Type", "application/json");
            params = JSON.toJSONString(parameters);
        } else {
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
    private static void close(DataOutputStream dos, HttpURLConnection conn) {
        if (dos != null) {
            try {
                dos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.disconnect();
            } catch (Exception e2) {
            }
        }
    }
    /**
     * 获取请求地址中的某个参数
     * @param url
     * @param name
     * @return
     */
    public static String getUrlParam(String url, String name) {
        return urlSplit(url).get(name);
    }

    /**
     * 去掉url中的路径，留下请求参数部分
     * @param url url地址
     * @return url请求参数部分
     */
    private static String truncateUrlPage(String url) {
        url = url.split("#")[0];
        String strAllParam = null;
        String[] arrSplit = null;
        url = url.trim();
        arrSplit = url.split("[?]");
        if (url.length() > 1) {
            if (arrSplit.length > 1) {
                for (int i = 1; i < arrSplit.length; i++) {
                    strAllParam = arrSplit[i];
                }
            }
        }
        return strAllParam;
    }

    /**
     * 将参数存入map集合
     * @param url  url地址
     * @return url请求参数部分存入map集合
     */
    public static Map<String, String> urlSplit(String url) {
        Map<String, String> mapRequest = new HashMap<String, String>();
        String[] arrSplit = null;
        String strUrlParam = truncateUrlPage(url);
        if (strUrlParam == null) {
            return mapRequest;
        }
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");
            //解析出键值
            if (arrSplitEqual.length > 1) {
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            } else {
                if (arrSplitEqual[0] != "") {
                    //只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }
}
