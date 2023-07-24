package com.villa.config;

import com.alibaba.fastjson.JSON;
import com.villa.dto.ResultDTO;
import com.villa.util.ThreadLocalUtil;
import com.villa.util.Util;
import com.villa.util.encrypt.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class VillaResponseBodyAdvice implements ResponseBodyAdvice<ResultDTO> {
    @Autowired
    private VillaConfig villaConfig;

    public boolean supports(MethodParameter methodParameter, Class aClass) {
        if(methodParameter.getMethod().getReturnType() == ResultDTO.class){
            return true;
        }
        return false;
    }
    @Override
    public ResultDTO beforeBodyWrite(ResultDTO resultDTO, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        //需要加密返回结果才加密
        if (villaConfig.isEncryptFlag()&&resultDTO.getData()!=null){
            String uri = serverHttpRequest.getURI().getPath();
            if(villaConfig.getExcludeUris().contains(uri)){
                return resultDTO;
            }
            if(Util.isNullOrEmpty(villaConfig.getEncryptURI())||(Util.isNotNullOrEmpty(villaConfig.getEncryptURI())&&Util.isNotNullOrEmpty(uri)&&uri.contains(villaConfig.getEncryptURI()))){
                String newData = JSON.toJSONString(resultDTO.getData());
                //兼容字符串转json时，会带上一对双引号的问题
                if(resultDTO.getData() instanceof String){
                    newData = newData.replace("\"","");
                }
                resultDTO.setData(EncryptionUtil.encryptAES(newData, ThreadLocalUtil.get().toString()));
            }
        }
        return resultDTO;
    }
}
