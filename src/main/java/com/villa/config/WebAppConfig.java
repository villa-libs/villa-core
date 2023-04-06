package com.villa.config;

import com.villa.redis.config.RedisConfiguration;
import com.villa.util.SpringContextUtil;
import com.villa.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableConfigurationProperties(RedisConfiguration.class)
@Configuration
public class WebAppConfig implements WebMvcConfigurer {
    //是否开启参数防串改配置
    @Value("${villa.param.distort:false}")
    private boolean paramDistort;
    @Value("${villa.interceptor.flag:false}")
    private boolean interceptorFlag;
    @Value("${villa.encrypt.flag:false}")
    private boolean encryptFlag;
    @Value("${villa.whitelist:}")
    private String whiteList;
    @Value("${villa.blacklist.flag:false}")
    private boolean blacklistFlag;
    //是否开启指定uri签名
    @Value("${villa.encrypt.uri:}")
    private String encryptURI;
    @Autowired
    private RedisConfiguration redisConfiguration;
    public void addInterceptors(InterceptorRegistry registry) {
        //如果拦截器 参数防篡改 参数加密 redis 白名单访问 黑名单限制 任意一个开启  最终拦截器都将开启 这些全部关闭才关闭
        if(interceptorFlag||encryptFlag||Util.isNotNullOrEmpty(encryptURI)||paramDistort|| //拦截器开关、参数加密、参数防串改
                blacklistFlag|| Util.isNotNullOrEmpty(whiteList)|| //黑名单开关、白名单列表
                Util.isNotNullOrEmpty(redisConfiguration.getHost())||redisConfiguration.getCluster().getNodes().size()>0//redis、
                ){
            /**
             * 1. 拦截/api和/back和/public开头的url
             * /public开头的url无需任何验证
             */
            registry.addInterceptor(SpringContextUtil.getBean(AuthorizationInterceptor.class))
                    .addPathPatterns("/api/**")
                    .addPathPatterns("/back/**")
                    .addPathPatterns("/public/**")
            ;
        }
    }
    /**
     * 支持全网跨域
     */
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
                .maxAge(3600)
                .allowCredentials(true);
    }
}