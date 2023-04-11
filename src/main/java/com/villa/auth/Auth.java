package com.villa.auth;

import com.alibaba.fastjson.JSON;
import com.villa.log.Log;
import com.villa.redis.RedisClient;
import com.villa.util.EncryptionUtil;
import com.villa.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.util.Base64;
import java.util.HashMap;

/**
 * @作者 微笑い一刀
 * @bbs_url https://blog.csdn.net/u012169821
 *
 * 授权相关的处理类
 */
@Component
public class Auth {
    /** 如果值为空 就使用redis+token方式 否则使用JWT(模拟的JWT，本身不是使用JWT原实现)方式 */
    @Value("${villa.auth.secret:}")
    private String secret;
    /**
     * 单一登录 默认开启
     * 单一登录 一个网站一个账号只能有一个在登录状态
     * 单点登录 一个token可以登录多个不同的系统
     * */
    @Value("${villa.auth.single:true}")
    private boolean single;
    /** session模式 也就是在最终失效时间内 超过分钟如果无操作 此token将会失效 */
    @Value("${villa.auth.session-model:false}")
    private boolean sessionModel;
    /** token失效时间 默认1个月 秒为单位 */
    @Value("${villa.auth.delay:2592000}")
    private int delay;
    @Autowired
    private RedisClient redisClient;

    public AuthModel create(){
        return new AuthModel(this);
    }
    void putMapper(String attrsJson, String token) {
        redisClient.set(attrsJson,token,delay);
    }
    void checkToken(String json){
        String token = redisClient.get(json);
        //自定义属性对应存在token
        if(Util.isNotNullOrEmpty(token)){
            //删除此token
            redisClient.del(token);
            //删除属性与token的映射
            redisClient.del(json);
        }
    }
    /** 只能同包访问 */
    void put(String token,AuthModel authModel){
        //模型中记录一个失效时间  用来处理失效 或重置失效时间的基数
        authModel.setFailureTime(authModel.getRequestLastTime() + delay * 1000L);
        redisClient.set(token,authModel,delay);
    }
    /**
     * 验证此token的有效性
     * */
    public boolean validate(String token){
        if (Util.isNullOrEmpty(token)){
            Log.err("【登录失败】token为空");
            return false;
        }
        AuthModel authModel = getAuthModel(token);
        if(Util.isNull(authModel)){
            Log.err("【登录失败】缓存中根据token获取授权模型为null");
            return false;
        }
        long curTime = System.currentTimeMillis();
        //JWT
        if(Util.isNotNullOrEmpty(secret)){
            //判断时间
            if(curTime > (long)authModel.getAttrs().get("expiration")){
                Log.err("【登录失败】JWT凭证已过期");
                redisClient.del(token);
                return false;
            }
        }
        //判断session模式 如果开启 并超过30分钟没访问
        if(sessionModel&&curTime > authModel.getRequestLastTime()+1000*60*30){
            Log.err("【登录失败】token逻辑失效,超出30分钟未访问");
            redisClient.del(token);
            return false;
        }
        //更新请求时间
        setRequestLastTime(authModel,curTime,token);
        return true;
    }

    /**
     * 根据token获取AuthModel
     */
    public AuthModel getAuthModel(String token){
        if(Util.isNullOrEmpty(token)){
            return null;
        }
        if(Util.isNotNullOrEmpty(secret)&&token.indexOf(".")!=-1){
            try{
                //.点 需要转义
                String[] infos = token.split("\\.");
                String data = new String(Base64.getUrlDecoder().decode(infos[0]));
                String sign = EncryptionUtil.encrypt_HMAC_SHA256(secret, data);
                //判断签名
                if(!sign.equals(infos[1])){
                    return null;
                }
                AuthModel authModel = new AuthModel();
                authModel.setAttrs(JSON.parseObject(data, HashMap.class));
                return authModel;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        return redisClient.get(token);
    }
    private void setRequestLastTime(AuthModel authModel,long curTime,String token){
        authModel.setRequestLastTime(curTime);
        //失效时间获取失败 得到的结果可能为负数
        int newDelay = (int)(authModel.getFailureTime() - curTime)/1000;
        if(newDelay<=0){
            newDelay = delay;
        }
        redisClient.set(token,authModel,newDelay);
    }
    public void logout(String token){
        redisClient.del(token);
    }
    public String getSecret() {
        return secret;
    }

    public boolean isSingle() {
        return single;
    }

    public boolean isSessionModel() {
        return sessionModel;
    }

    public int getDelay() {
        return delay;
    }
}
