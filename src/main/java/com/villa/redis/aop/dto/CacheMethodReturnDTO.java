package com.villa.redis.aop.dto;

public class CacheMethodReturnDTO {
    private boolean isArray;
    private Class clz;

    public boolean isArray() {
        return isArray;
    }

    public void setArray(boolean array) {
        isArray = array;
    }

    public Class getClz() {
        return clz;
    }

    public void setClz(Class clz) {
        this.clz = clz;
    }
}
