package com.villa.config;

import com.villa.util.EncryptionUtil;
import com.villa.util.ThreadLocalUtil;
import com.villa.util.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @作者 微笑い一刀
 * @bbs_url https://blog.csdn.net/u012169821
 */
@ControllerAdvice
public class VillaRequestBodyAdvice implements RequestBodyAdvice {
    @Value("${villa.encrypt.flag:false}")
    private boolean encryptFlag;
    //是否开启指定uri签名
    @Value("${villa.encrypt.uri:}")
    private String encryptURI;
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        //需要加密才加密
        if(!encryptFlag){
            return httpInputMessage;
        }
        String uri = getRequestURI(methodParameter);
        if(Util.isNullOrEmpty(encryptURI)||(Util.isNotNullOrEmpty(encryptURI)&&Util.isNotNullOrEmpty(uri)&&uri.contains(encryptURI))){
            String srcBody = StreamUtils.copyToString(httpInputMessage.getBody(), Charset.forName("utf-8"));
            if(Util.isNotNullOrEmpty(srcBody)){
                try{
                    String body = EncryptionUtil.decrypt_AES(srcBody, EncryptionUtil.getSign(Long.parseLong(httpInputMessage.getHeaders().getFirst("timestamp"))));
                    return new MyHttpInputMessage(httpInputMessage.getHeaders(),body.getBytes(StandardCharsets.UTF_8));
                }catch (Exception e){}
            }
        }
        return httpInputMessage;
    }
    public Object afterBodyRead(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return o;
    }
    public Object handleEmptyBody(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return o;
    }

    private String getRequestURI(MethodParameter methodParameter){
        String classURI = "";
        if(methodParameter.getContainingClass().getAnnotation(RequestMapping.class)!=null){
            classURI = methodParameter.getContainingClass().getAnnotation(RequestMapping.class).value()[0];
        }else if(methodParameter.getContainingClass().getAnnotation(GetMapping.class)!=null){
            classURI = methodParameter.getContainingClass().getAnnotation(GetMapping.class).value()[0];
        }if(methodParameter.getContainingClass().getAnnotation(PostMapping.class)!=null){
            classURI = methodParameter.getContainingClass().getAnnotation(PostMapping.class).value()[0];
        }


        String methodURI = "";
        if(methodParameter.getMethodAnnotation(RequestMapping.class)!=null){
            methodURI = methodParameter.getMethodAnnotation(RequestMapping.class).value()[0];
        }else if(methodParameter.getMethodAnnotation(GetMapping.class)!=null){
            methodURI = methodParameter.getMethodAnnotation(GetMapping.class).value()[0];
        }else if(methodParameter.getMethodAnnotation(PostMapping.class)!=null){
            methodURI = methodParameter.getMethodAnnotation(PostMapping.class).value()[0];
        }
        return classURI+methodURI;
    }

    public static class MyHttpInputMessage implements HttpInputMessage {

        public MyHttpInputMessage(HttpHeaders headers, byte[] body) {
            this.headers = headers;
            this.body = body;
        }

        private HttpHeaders headers;

        private byte[] body;

        public InputStream getBody() throws IOException {
            return body==null?null:new ByteArrayInputStream(body);
        }

        public HttpHeaders getHeaders() {
            return headers;
        }
    }
}
