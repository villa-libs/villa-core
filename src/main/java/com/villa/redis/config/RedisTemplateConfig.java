package com.villa.redis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableConfigurationProperties(RedisConfiguration.class)
public class RedisTemplateConfig {
    @Autowired
    private RedisConfiguration redisConfiguration;
    @Bean
    public RedisTemplate<Object, Object> redisTemplate() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(redisConfiguration.getDatabase());
        redisStandaloneConfiguration.setHostName(redisConfiguration.getHost());
        redisStandaloneConfiguration.setPort(redisConfiguration.getPort());
        redisStandaloneConfiguration.setPassword(redisConfiguration.getPassword());
        //如果使用了SSL协议 使用不同配置
        LettuceClientConfiguration lettuceClientConfiguration =
                redisConfiguration.isSsl()?LettuceClientConfiguration.builder().useSsl().build():LettuceClientConfiguration.builder().build();
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration);
        factory.afterPropertiesSet(); // 必须初始化实例
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        // 自定义的string序列化器和fastjson序列化器
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // jackson 序列化器
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        // kv 序列化
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(jsonRedisSerializer);
        // hash 序列化
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(jsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
