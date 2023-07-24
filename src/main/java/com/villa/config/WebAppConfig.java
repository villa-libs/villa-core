package com.villa.config;

import com.villa.redis.config.RedisConfiguration;
import com.villa.util.SpringContextUtil;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableConfigurationProperties(RedisConfiguration.class)
@Configuration
public class WebAppConfig implements WebMvcConfigurer {
    public void addInterceptors(InterceptorRegistry registry) {
        /**
         * 拦截/api和/back和/public开头的url
         * /public开头的url无需任何验证
         */
        registry.addInterceptor(SpringContextUtil.getBean(AuthorizationInterceptor.class))
                .addPathPatterns("/api/**")
                .addPathPatterns("/back/**")
                .addPathPatterns("/public/**")
        ;
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