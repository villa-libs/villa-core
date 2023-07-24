package com.villa.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 将所有基础配置都整理到此模型
 */
@Component
public class VillaConfig {
    //参数加密
    @Value("${villa.encrypt.flag:false}")
    private boolean encryptFlag;
    //白名单
    @Value("${villa.whitelist:}")
    private String whiteList;
    //黑名单
    @Value("${villa.blacklist.flag:false}")
    private boolean blacklistFlag;
    /** 一秒超过多少次请求将进入黑名单 -1或0代表无限制 */
    @Value("${villa.blacklist.ipMax:-1}")
    private int ipMax;
    @Value("${villa.blacklist.delay:-1}")//多少秒后解除黑名单 -1-永不解除
    private int blacklistDelay;
    //是否开启指定uri签名
    @Value("${villa.encrypt.uri:}")
    private String encryptURI;
    //参数加密排除的URI 用逗号,隔开
    @Value("${villa.encrypt.excludeUri:}")
    private String excludeUri;
    private List<String> excludeUris = new ArrayList<>();
    //是否启用ssh连接 可用于mysql的ssh方式连接
    @Value("${villa.ssh.flag:false}")
    private boolean sshFlag;
    //签名校验的时间误差 默认60秒
    @Value("${villa.sign.delay:60}")
    private int signDelay;
    //参数加密使用的公钥
    @Value("${villa.encrypt.publicKey:}")
    private String publicKey;
    //参数加密使用的私钥
    @Value("${villa.encrypt.privateKey:}")
    private String privateKey;
    //参数加密使用的prime参数
    @Value("${villa.encrypt.prime:}")
    private String prime;
    //参数加密使用的本地密钥
    private String localEncryptSecret;

    public String getExcludeUri() {
        return excludeUri;
    }

    public void setExcludeUri(String excludeUri) {
        this.excludeUri = excludeUri;
    }

    public List<String> getExcludeUris() {
        return Arrays.asList(excludeUri.split(","));
    }

    public void setExcludeUris(List<String> excludeUris) {
        this.excludeUris = excludeUris;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPrime() {
        return prime;
    }

    public void setPrime(String prime) {
        this.prime = prime;
    }

    public String getLocalEncryptSecret() {
        return localEncryptSecret;
    }

    public void setLocalEncryptSecret(String localEncryptSecret) {
        this.localEncryptSecret = localEncryptSecret;
    }

    public boolean isEncryptFlag() {
        return encryptFlag;
    }

    public void setEncryptFlag(boolean encryptFlag) {
        this.encryptFlag = encryptFlag;
    }

    public String getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(String whiteList) {
        this.whiteList = whiteList;
    }

    public boolean isBlacklistFlag() {
        return blacklistFlag;
    }

    public void setBlacklistFlag(boolean blacklistFlag) {
        this.blacklistFlag = blacklistFlag;
    }

    public int getIpMax() {
        return ipMax;
    }

    public void setIpMax(int ipMax) {
        this.ipMax = ipMax;
    }

    public int getBlacklistDelay() {
        return blacklistDelay;
    }

    public void setBlacklistDelay(int blacklistDelay) {
        this.blacklistDelay = blacklistDelay;
    }

    public String getEncryptURI() {
        return encryptURI;
    }

    public void setEncryptURI(String encryptURI) {
        this.encryptURI = encryptURI;
    }

    public boolean isSshFlag() {
        return sshFlag;
    }

    public void setSshFlag(boolean sshFlag) {
        this.sshFlag = sshFlag;
    }

    public int getSignDelay() {
        return signDelay;
    }

    public void setSignDelay(int signDelay) {
        this.signDelay = signDelay;
    }
}
