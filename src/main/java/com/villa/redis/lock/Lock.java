package com.villa.redis.lock;

import com.villa.redis.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @作者 微笑い一刀
 * @bbs_url https://blog.csdn.net/u012169821
 */
@Component
public class Lock {
    //获取的区块号
    public static final String EVENT_LOCK_KEY = "lock";
    public static final int delay = 60*2;
    @Autowired
    private RedisClient redisClient;
    /**
     * 根据某个Key锁一定时间 以秒为单位
     */
    public boolean lock(String key,int s){
        return redisClient.setnx(key,1,s);
    }
    /**
     * 根据某个Key锁两分钟
     */
    public boolean lock(String key){
        return lock(key,delay);
    }
    public boolean lock(){
        return lock(EVENT_LOCK_KEY,delay);
    }
    /**
     * 释放某个Key对应的锁
     * @param key
     */
    public void unLock(String key){
        redisClient.del(key);
    }
    /**
     * 释放锁
     */
    public void unLock(){
        unLock(EVENT_LOCK_KEY);
    }
}
