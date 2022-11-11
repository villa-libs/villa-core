package com.villa.redis;

import com.alibaba.fastjson.JSON;
import com.villa.util.Util;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisClient {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    /**
     * 获取数据
     */
    public String getStr(String key) {
        if(Util.isNullOrEmpty(key))return null;
        Object result = redisTemplate.opsForValue().get(key);
        if(result == null)return null;
        if(result instanceof String){
            return (String) result;
        }
        return JSON.toJSONString(result);
    }
    public <T>T get(String key) {
        Object result = redisTemplate.opsForValue().get(key);
        if(result == null)return null;
        return (T)result;
    }

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key,value);
    }
    public void set(String key, Object value, int time) {
        redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    }
    /**
     * 放缓存并加锁 可以通过del(key)删除key来释放锁
     */
    public boolean setnx(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key,value);
    }
    public boolean setnx(String key, Object value, int time) {
        return redisTemplate.opsForValue().setIfAbsent(key,value,time,TimeUnit.SECONDS);
    }
    public void addSet(String key, Object ...values) {
        redisTemplate.opsForSet().add(key,values);
    }

    /**
     * 是否在Set集合中存在
     * @param key
     * @param value
     */
    public boolean existWithSet(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key,value);
    }
    public <T> Set<T> getSet(String key){
        return (Set<T>) redisTemplate.opsForSet().members(key);
    }
    /**
     * 存储redis队列 顺序存储 左进
     * @param key redis键名
     * @param value 键值
     */
    public void lpush(String key, Object value) {
        redisTemplate.opsForList().leftPush(key,value);
    }
    /**
     * 获取队列数据  右出
     * @param key 键名
     * @return
     */
    public Object rpop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }
    public void del(String key) {
        redisTemplate.delete(key);
    }
}
