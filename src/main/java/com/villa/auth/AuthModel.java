package com.villa.auth;

import com.alibaba.fastjson.JSON;
import com.villa.util.EncryptionUtil;
import com.villa.util.Util;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @作者 微笑い一刀
 * @bbs_url https://blog.csdn.net/u012169821
 */
public class AuthModel implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 自定义属性的封装集合 */
    private Map<String,Object> attrs = new HashMap<>();
    /** 上一次请求的时间 每次请求的时间不能相同 且可用此字段用来处理逻辑上的失效时间  无操作30分钟失效,但需要开启session-proxy模式*/
    private long requestLastTime;
    /** 失效时间 */
    private long failureTime;
    private Auth auth;
    public static final String SYSTEM_TYPE_KEY = "auth_prototype_system_type";
    public static final String AUTH_PROTOTYPE_ID = "auth_prototype_id";
    public static final String SYSTEM_TYPE = "admin";
    public AuthModel() {
    }

    public AuthModel(Auth auth){
        this.auth = auth;
        //如果JWT方式 在数据中组装失效时间
        if(Util.isNotNullOrEmpty(auth.getSecret())){
            long curTime = System.currentTimeMillis();
            putAttr("expiration", curTime+auth.getDelay()*1000);
            putAttr("createTime",curTime);
            return;
        }
        this.requestLastTime = System.currentTimeMillis();
    }
    /** 传入自定义属性 */
    public AuthModel putAttr(String key,Object value){
        attrs.put(key,value);
        return this;
    }
    public String createAdminToken(){
        attrs.put(SYSTEM_TYPE_KEY,SYSTEM_TYPE);
        return createToken();
    }
    /**
     * 创建token
     */
    public String createToken(){
        //如果不是单点 往自定义属性集合中添加uuid用作区别
        String attrsJson = JSON.toJSONString(attrs);
        if(Util.isNotNullOrEmpty(auth.getSecret())){
            return Base64.getUrlEncoder().encodeToString(attrsJson.getBytes(StandardCharsets.UTF_8))+"."+EncryptionUtil.encrypt_HMAC_SHA256(auth.getSecret(),attrsJson);
        }
        putAttr(AUTH_PROTOTYPE_ID, UUID.randomUUID().toString());
        //单点登录时 使用自定义属性(未设置UUID的) 查看是否存在token,存在就进行删除
        if(auth.isSingle()){
            auth.checkToken(attrsJson);
        }
        //通过设置了UUID的自定义属性创建token 保持每次得到的token不一致
        String token = EncryptionUtil.encrypt_MD5(JSON.toJSONString(attrs));
        //存映射关系
        if(auth.isSingle()){
            auth.putMapper(attrsJson,token);
        }
        //存放映射关系
        auth.put(token,this);
        return token;
    }

    public Map<String, Object> getAttrs() {
        return attrs;
    }
    public void setAttrs(Map<String, Object> attrs) {
        this.attrs = attrs;
    }
    public long getFailureTime() {return failureTime;}
    public void setFailureTime(long failureTime) {
        this.failureTime = failureTime;
    }
    public long getRequestLastTime() {
        return requestLastTime;
    }
    public void setRequestLastTime(long requestLastTime) {
        this.requestLastTime = requestLastTime;
    }
}
