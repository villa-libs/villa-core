package com.villa.auth;

import com.alibaba.fastjson.JSON;
import com.villa.redis.RedisClient;
import com.villa.log.Log;
import com.villa.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @作者 微笑い一刀
 * @bbs_url https://blog.csdn.net/u012169821
 *
 * 授权相关的处理类
 */
@Component
public class Auth {
    /** 如果值不为jvm,则使用redis */
    @Value("${villa.auth.storage:redis}")
    private String storage;
    /** 是否开启单点登录 默认开启 */
    @Value("${villa.auth.sso:true}")
    private boolean sso;
    /** session模式 也就是在最终失效时间内 超过分钟如果无操作 此token将会失效 */
    @Value("${villa.auth.session-model:false}")
    private boolean sessionModel;
    /** token失效时间 默认1个月 秒为单位 */
    @Value("${villa.auth.delay:2592000}")
    private int delay;

    @Autowired
    private RedisClient redisClient;
    private static boolean flag;
    private static Map<String,AuthModel> authModels = new ConcurrentHashMap<>();
    private static Map<String,String> jsonTokenMapper = new ConcurrentHashMap<>();

    public AuthModel create(){
        start();
        return new AuthModel(sso,this);
    }
    void putMapper(String attrsJson, String token) {
        if(!"redis".equalsIgnoreCase(storage)){
            jsonTokenMapper.put(attrsJson,token);
            return;
        }
        redisClient.set(attrsJson,token,delay);
    }
    void checkToken(String json){
        String token;
        if(!"redis".equalsIgnoreCase(storage)){
            token = jsonTokenMapper.get(json);
        }else{
            token = redisClient.get(json);
        }
        //自定义属性对应存在token
        if(Util.isNotNullOrEmpty(token)){
            //删除此token
            removeToken(token);
            //删除属性与token的映射
            if(!"redis".equalsIgnoreCase(storage)){
                jsonTokenMapper.remove(json);
                return;
            }
            redisClient.del(json);
        }
    }
    /** 只能同包访问 */
    void put(String token,AuthModel authModel){
        //模型中记录一个失效时间  用来处理失效 或重置失效时间的基数
        authModel.setFailureTime(authModel.getRequestLastTime() + delay * 1000L);
        if(!"redis".equalsIgnoreCase(storage)){
            authModels.put(token,authModel);
            return;
        }
        redisClient.set(token,authModel,delay);
    }
    /**
     * 一分钟执行一次的定时器
     * 检查token是否失效 失效就删除
     */
    private void start(){
        //如果使用redis做缓存 则不启动定时器
        if("redis".equalsIgnoreCase(storage)){
            return;
        }
        if(flag){
            return;
        }
        flag = true;
        new Timer().schedule(new TimerTask() {
            public void run() {
                long curTime = System.currentTimeMillis();
                Iterator<String> iterator = authModels.keySet().iterator();
                while (iterator.hasNext()){
                    String token = iterator.next();
                    AuthModel authModel = authModels.get(token);
                    //如果当前时间大于失效时间  删除此token
                    if(curTime >= authModel.getFailureTime()){
                        //删除登录token
                        authModels.remove(token);
                        //删除属性与token的映射
                        Map<String, Object> attrs = authModel.getAttrs();
                        attrs.remove("auth_prototype_id");
                        jsonTokenMapper.remove(JSON.toJSONString(attrs));
                    }
                }
            }
        },500,60*1000);
    }
    /**
     * 验证此token的有效性
     * */
    public boolean validate(String token,long curTime){
        if (Util.isNullOrEmpty(token)){
            Log.err("【登录失败】token为空");
            return false;
        }
        AuthModel authModel = getAuthModel(token);
        if(Util.isNull(authModel)){
            Log.err("【登录失败】缓存中根据token获取授权模型为null");
            return false;
        }
        //内存存储模式 超过失效时间
        if(!"redis".equalsIgnoreCase(storage)&&curTime >= authModel.getFailureTime()){
            Log.err("【登录失败】token逻辑失效,超出30分钟未访问");
            removeToken(token);
            return false;
        }
        //判断session模式 如果开启 并超过30分钟没访问
        if(sessionModel&&curTime > authModel.getRequestLastTime()+1000*60*30){
            Log.err("【登录失败】token逻辑失效,超出30分钟未访问");
            removeToken(token);
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
        if(!"redis".equalsIgnoreCase(storage)){
            return authModels.get(token);
        }
        return redisClient.get(token);
    }
    private void setRequestLastTime(AuthModel authModel,long curTime,String token){
        authModel.setRequestLastTime(curTime);
        if(!"redis".equalsIgnoreCase(storage)){
            //更新了最后访问事件 覆盖模型
            authModels.put(token,authModel);
            return;
        }
        //失效时间获取失败 得到的结果可能为负数
        int newDelay = (int)(authModel.getFailureTime() - curTime)/1000;
        if(newDelay<=0){
            newDelay = delay;
        }
        redisClient.set(token,authModel,newDelay);
    }
    private void removeToken(String token){
        if(!"redis".equalsIgnoreCase(storage)){
            authModels.remove(token);
            return;
        }
        redisClient.del(token);
    }
}
