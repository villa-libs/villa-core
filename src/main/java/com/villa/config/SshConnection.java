package com.villa.config;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.villa.log.Log;
import com.villa.util.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SshConnection {
    @Value("${villa.ssh.username:}")
    String username;
    @Value("${villa.ssh.password:}")
    String password;
    @Value("${villa.ssh.host:}")
    String host;
    @Value("${villa.ssh.port:22}")
    int port;

    //本地端口
    @Value("${villa.ssh.local-port:3306}")
    int local_port;
    @Value("${villa.ssh.local-host:127.0.0.1}")
    String local_host;
    @Value("${villa.ssh.remote-port:3306}")
    int remote_port;
    Session session;

    /**
     * 建设SSH连贯
     */
    public void init() {
        try {
            JSch jsch = new JSch();
            Util.assertionIsNotNullOrEmpty(username,"ssh用户名必填[villa.ssh.username]");
            Util.assertionIsNotNullOrEmpty(password,"ssh密码必填[villa.ssh.password]");
            Util.assertionIsNotNullOrEmpty(host,"ssh主机ip或域名必填[villa.ssh.host]");
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            session.setPortForwardingL(local_port, local_host, remote_port);
            Log.out("【SSH链接】启动成功!\tip: %s\tlocal-port: %d==>\tremote-port:%d",host,local_port,remote_port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 断开SSH连贯
     */
    public void destroy() {
        this.session.disconnect();
    }
}