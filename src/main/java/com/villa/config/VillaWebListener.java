package com.villa.config;

import com.villa.log.LogConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Integer.MIN_VALUE)
public class VillaWebListener implements ApplicationRunner {
    @Autowired
    private VillaConfig villaConfig;
    @Autowired
    private LogConfig logConfig;
    @Autowired(required = false)
    private SshConnection sshConnection;
    public void run(ApplicationArguments args) {
        //初始化日志组件
        logConfig.init();
        //初始化SSH转发工具
        if(villaConfig.isSshFlag())sshConnection.init();
        if(!villaConfig.isEncryptFlag())return;
    }
}
