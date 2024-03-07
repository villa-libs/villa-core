package com.villa.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
/**@author system*/
@Data
public class BaseEntity<T> {

    @TableId(type = IdType.AUTO)
    private Long id;//自动填充ID
    @TableField("create_admin")
    private Long createAdmin;//创建人 通过AOP来设置值
    @TableField("update_admin")
    private Long updateAdmin;//更新人 通过AOP来设置值
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Long createTime;//创建时间
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;//更新时间
    @Version
    @TableField(fill = FieldFill.INSERT)
    private Long version;//乐观锁
    @JsonIgnore
    public QueryWrapper<T> getWrapper(){return new QueryWrapper<>();}
}