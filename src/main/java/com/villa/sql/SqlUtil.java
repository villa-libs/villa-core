package com.villa.sql;

import com.villa.comm.CallBack;
import com.villa.dto.ErrCodeDTO;
import com.villa.log.Log;

import java.util.List;

public class SqlUtil {
    /**
     * 执行dml操作
     * @param callBack
     * @return 返回受影响行数
     */
    public static int dmlSql(CallBack callBack){
        try {
            Object row = callBack.callback();
            return row==null?0:(Integer)row;
        }catch(Exception e){
            Log.err(e.getMessage());
            throw new RuntimeException(ErrCodeDTO.ox99999);
        }
    }

    /**
     * 执行dql操作 获取单条数据
     */
    public static <T> T dqlSqlGetOne(CallBack callBack){
        try {
            return (T)callBack.callback();
        }catch(Exception e){
            Log.err(e.getMessage());
            throw new RuntimeException(ErrCodeDTO.ox99999);
        }
    }

    /**
     * 执行dql操作 获取多条数据
     */
    public static <T> List<T> dqlSqlGetList(CallBack callBack){
        try {
            return (List<T>)callBack.callback();
        }catch(Exception e){
            Log.err(e.getMessage());
            throw new RuntimeException(ErrCodeDTO.ox99999);
        }
    }
}
