package com.villa.config;

import com.alibaba.fastjson.JSON;
import com.villa.dto.ResultDTO;
import com.villa.log.Log;
import com.villa.util.EncryptionUtil;
import com.villa.util.ThreadLocalUtil;
import com.villa.util.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @作者 微笑い一刀
 * @bbs_url https://blog.csdn.net/u012169821
 */
@ControllerAdvice
public class VillaResponseBodyAdvice implements ResponseBodyAdvice<ResultDTO> {
    @Value("${villa.encrypt.flag:false}")
    private boolean encryptFlag;
    //是否开启指定uri签名
    @Value("${villa.encrypt.uri:}")
    private String encryptURI;
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        if(methodParameter.getMethod().getReturnType() == ResultDTO.class){
            return true;
        }
        return false;
    }
    @Override
    public ResultDTO beforeBodyWrite(ResultDTO resultDTO, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        //需要加密返回结果才加密
        if (encryptFlag&&resultDTO.getData()!=null){
            String uri = serverHttpRequest.getURI().getPath();
            if(Util.isNullOrEmpty(encryptURI)||(Util.isNotNullOrEmpty(encryptURI)&&Util.isNotNullOrEmpty(uri)&&uri.contains(encryptURI))){
                long timestamp = System.currentTimeMillis();
                String newData = JSON.toJSONString(resultDTO.getData());
                //兼容字符串转json时，会带上一对双引号的问题
                if(resultDTO.getData() instanceof String){
                    newData = newData.replace("\"","");
                }
                resultDTO.setData(EncryptionUtil.encrypt_AES(newData,EncryptionUtil.getSign(timestamp)));
                resultDTO.setTimestamp(timestamp);
            }
        }
        return resultDTO;
    }
}
