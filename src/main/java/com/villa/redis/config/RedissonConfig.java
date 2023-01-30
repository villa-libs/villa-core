package com.villa.redis.config;

import com.villa.util.Util;
import org.redisson.Redisson;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RedissonConfig {
    @Autowired(required = false)
    private RedisConfiguration redisConfiguration;

    /**
     * 集群模式-添加redisson的bean
     * 从spring-redis中取配置信息 不需要redisson的配置了
     */
    @Bean
    public Redisson redisson() {
        String protocol = redisConfiguration.isSsl()?"rediss":"redis";
        System.out.println("使用协议："+protocol);
        //单机版
        if(Util.isNotNullOrEmpty(redisConfiguration.getHost())){
            Config config = new Config();
            String redisUrl = String.format(protocol+"://%s:%s", redisConfiguration.getHost(), redisConfiguration.getPort());
            config.useSingleServer().setAddress(redisUrl);
            if(Util.isNotNullOrEmpty(redisConfiguration.getPassword())){
                config.useSingleServer().setPassword(redisConfiguration.getPassword());
            }
            config.useSingleServer().setDatabase(redisConfiguration.getDatabase());
            return (Redisson)Redisson.create(config);
        }
        //集群版
        List<String> clusterNodes = new ArrayList<>();
        for (int i = 0; i < redisConfiguration.getCluster().getNodes().size(); i++) {
            clusterNodes.add(protocol+"://" + redisConfiguration.getCluster().getNodes().get(i));
        }
        Config config = new Config();
        ClusterServersConfig clusterServersConfig = config.useClusterServers()
                .addNodeAddress(clusterNodes.toArray(new String[clusterNodes.size()]));
        if (Util.isNotNullOrEmpty(redisConfiguration.getPassword())){
            clusterServersConfig.setPassword(redisConfiguration.getPassword());
        }
        return (Redisson) Redisson.create(config);
    }
}
