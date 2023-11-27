package com.villa.entity;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

public interface BaseEntity<T> {
    QueryWrapper<T> getWrapper();
}
