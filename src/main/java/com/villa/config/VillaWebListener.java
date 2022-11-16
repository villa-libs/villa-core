package com.villa.config;

import com.villa.log.LogConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @作者 微笑い一刀
 * @bbs_url https://blog.csdn.net/u012169821
 */
@Component
public class VillaWebListener implements ApplicationRunner {
    @Value("${i18n:false}")
    private boolean i18n;
    @Value("${villa.ssh.flag:false}")
    private boolean sshFlag;
    @Autowired
    private LogConfig logConfig;
    @Autowired(required = false)
    private SshConnection sshConnection;
    public void run(ApplicationArguments args) {
        //初始化日志组件
        logConfig.init();
        //初始化SSH转发工具
        if(sshFlag){
            sshConnection.init();
        }
    }
}
