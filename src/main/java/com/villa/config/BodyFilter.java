package com.villa.config;

import com.villa.auth.Auth;
import com.villa.auth.AuthModel;
import com.villa.util.ThreadLocalUtil;
import com.villa.util.Util;
import com.villa.util.encrypt.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(urlPatterns = "/*",filterName = "BodyFilter")
@Component
public class BodyFilter implements Filter {
    @Autowired
    private VillaConfig villaConfig;
    @Autowired
    private Auth auth;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String reqURI = httpRequest.getRequestURI();
        /**
         * 1. 如果未开启加密
         * 2. 如果是放行的路径
         * 直接放行
         */
        if(!villaConfig.isEncryptFlag() || villaConfig.getExcludeUris().contains(reqURI)){
            chain.doFilter(request, response);
            return;
        }
        /**
         * 1. 上传请求 无需包装
         * 2. 被排除加密的URI 一般是注册登录这些
         */
        if(Util.isNotNullOrEmpty(request.getContentType())&&request.getContentType().startsWith("multipart/")){
            handlerSecret(httpRequest);
            chain.doFilter(request, response);
            return;
        }
        //需要参数加密才包装请求对象
        if (request instanceof HttpServletRequest&& villaConfig.isEncryptFlag()) {
            handlerSecret(httpRequest);
            //没有指定加密路径 或当前路径是指定的加密路径
            if(Util.isNullOrEmpty(villaConfig.getEncryptURI())||
                (Util.isNotNullOrEmpty(villaConfig.getEncryptURI())&&httpRequest.getRequestURI().contains(villaConfig.getEncryptURI()))){
                //自定义请求对象包装器 为了ClassUtil.getParamStr()能获取到参数 并做参数防串改验证
                BodyReaderHttpServletRequestWrapper requestWrapper = new BodyReaderHttpServletRequestWrapper(httpRequest);
                chain.doFilter(requestWrapper,response);
                return;
            }
        }
        chain.doFilter(request, response);
    }
    //计算当前用户的密钥 并放入当前线程
    private void handlerSecret(HttpServletRequest request){
        String token = request.getHeader("v-token");
        if(Util.isNullOrEmpty(token))return;
        AuthModel authModel = auth.getAuthModel(token);
        if(authModel == null)return;
        String publicKey = (String) authModel.getAttrs().get("publicKey");
        Util.assertionIsNotNullOrEmpty(publicKey,"用户加密公钥为空");
        //生成密钥
        String aesKey = EncryptionUtil.sharedAESKey(publicKey, villaConfig.getPrivateKey(), villaConfig.getPrime());
        ThreadLocalUtil.set(aesKey);
    }
}
