package com.villa.config;

import com.villa.util.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @作者 微笑い一刀
 * @bbs_url https://blog.csdn.net/u012169821
 */
@WebFilter(urlPatterns = "/*",filterName = "BodyFilter")
@Component
public class BodyFilter implements Filter {
    //是否开启参数防串改配置
    @Value("${villa.param.distort:false}")
    private boolean paramDistort;
    //是否开启参数加密
    @Value("${villa.encrypt.flag:false}")
    private boolean encryptFlag;
    //是否开启指定uri签名
    @Value("${villa.encrypt.uri:}")
    private String encryptURI;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        //上传请求 无需包装
        if(Util.isNotNullOrEmpty(request.getContentType())&&request.getContentType().startsWith("multipart/")){
            chain.doFilter(request, response);
            return;
        }
        //需要防串改或参数加密才包装请求对象
        if (request instanceof HttpServletRequest&&(paramDistort||encryptFlag)) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            if(Util.isNullOrEmpty(encryptURI)||(Util.isNotNullOrEmpty(encryptURI)&&httpRequest.getRequestURI().contains(encryptURI))){
                //自定义请求对象包装器 为了ClassUtil.getParamStr()能获取到参数 并做参数防串改验证
                BodyReaderHttpServletRequestWrapper requestWrapper = new BodyReaderHttpServletRequestWrapper(httpRequest);
                chain.doFilter(requestWrapper,response);
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
