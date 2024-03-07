package com.villa.dto;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.villa.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**@author system*/
public class PageDTO<T extends BaseEntity> {
    @Setter
    @Getter
    private long page;//当前页
    @Setter
    @Getter
    private long pageSize;//每页显示条数
    @Getter
    private long total;//总记录数
    @Setter
    @Getter
    private long pageCount;//尾页
    @Setter
    private List<T> data;//数据集
    @Setter
    @Getter
    private T clzObj;//用作高级查询
    public void setTotal(long total) {
        this.total = total;
        if (this.pageSize < 1L || this.pageSize > 100L) {
            this.pageSize = 10L;
        }

        this.pageCount = total / this.pageSize + (long)(total % this.pageSize != 0L ? 1 : 0);
        if (this.page > this.pageCount) {
            this.page = this.pageCount;
        }
        if (this.page < 1L) {
            this.page = 1L;
        }
    }

    public List<T> getData() {
        if (null == this.data) {
            this.data = new ArrayList<T>();
        }
        return this.data;
    }

    @JsonIgnore
    public long getFromRowIndex() {
        return (this.page - 1L) * this.pageSize;
    }

    @JsonIgnore
    public QueryWrapper getPageWrapper() {
        if(getClzObj() != null){
            return (QueryWrapper) getClzObj().getWrapper().last(" limit " + getFromRowIndex() + "," + getPageSize());
        }else{
            QueryWrapper wrapper = new QueryWrapper<>();
            wrapper.last(" limit " + getFromRowIndex() + "," + getPageSize());
            return wrapper;
        }
    }
}