package com.villa.dto;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.villa.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;

public class PageDTO<T extends BaseEntity> {
    private long page;//当前页
    private long pageSize;//每页显示条数
    private long total;//总记录数
    private long pageCount;//尾页
    private List<T> data;//数据集
    private T obj;//用作高级查询
    public PageDTO() {
    }
    public long getPage() {
        return this.page;
    }
    public void setPage(long page) {
        this.page = page;
    }
    public long getPageSize() {
        return this.pageSize;
    }
    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return this.total;
    }

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

    public long getPageCount() {
        return this.pageCount;
    }

    public void setPageCount(long pageCount) {
        this.pageCount = pageCount;
    }

    public List<T> getData() {
        if (null == this.data) {
            this.data = new ArrayList<T>();
        }

        return this.data;
    }

    public void setData(List<T> dataList) {
        this.data = dataList;
    }

    public long getFromRowIndex() {
        return (this.page - 1L) * this.pageSize;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public QueryWrapper<T> getPageWrapper() {
        if(getObj() != null){
            return (QueryWrapper<T>) getObj().getWrapper().last(" limit " + getFromRowIndex() + "," + getPageSize());
        }else{
            QueryWrapper<T> wrapper = new QueryWrapper<>();
            wrapper.last(" limit " + getFromRowIndex() + "," + getPageSize());
            return wrapper;
        }
    }
}