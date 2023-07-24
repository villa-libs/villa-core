package com.villa.util;

import com.villa.comm.CallBack;
import com.villa.comm.NoReturnCallBack;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

/**
 * 专用于断言的工具类
 */
public class Assertion {
    public static final String assertionIsTrue = "assertionIsTrue";
    public static final String assertionIsFalse = "assertionIsFalse";
    public static final String assertionIsNumber = "assertionIsNumber";
    public static final String assertionIsUrl = "assertionIsUrl";
    public static final String assertionIsEmail = "assertionIsEmail";
    public static final String assertionIsPhone = "assertionIsPhone";
    public static final String assertionIsNumberNotEq = "assertionIsNumberNotEq";
    public static final String assertionIsNumberEq = "assertionIsNumberEq";
    public static final String assertionIsStrEq = "assertionIsStrEq";
    public static final String assertionIsStrNotEq = "assertionIsStrNotEq";
    public static final String assertionIsNull = "assertionIsNull";
    public static final String assertionIsNotNull = "assertionIsNotNull";
    public static final String assertionIsNotNullOrEmpty = "assertionIsNotNullOrEmpty";
    public static final String assertionIsIdCard = "assertionIsIdCard";
    public static final String assertionIsEmptyCollection = "assertionIsEmptyCollection";
    public static final String assertionIsNotEmptyCollection = "assertionIsNotEmptyCollection";
    public static final String assertionNoException = "assertionNoException";
    public static final String assertionIsRangeLength = "assertionIsRangeLength";
    public static final String assertionIsCarNum = "assertionIsCarNum";
    public static final String assertionIsOnlyStrAndNum = "assertionIsOnlyStrAndNum";
    public static final String assertionIsStrAndNum = "assertionIsStrAndNum";
    public static final String assertionIsUpLow = "assertionIsUpLow";
    public static final String assertionIsUpLowNum = "assertionIsUpLowNum";
    public static final String assertionIsUpLowNumChar = "assertionIsUpLowNumChar";
    public static final String assertionIsUpOneAndLowNum = "assertionIsUpOneAndLowNum";
    public static final String assertionIsUpOneAndLowNumChar = "assertionIsUpOneAndLowNumChar";
    public static final String assertionIsGtZero = "assertionIsGtZero";
    public static final String assertionIsMax = "assertionIsMax";
    public static final String assertionIsMin = "assertionIsMin";

    /**
     * 断言是结果为false	为true则报错
     * 可被Validate注解使用
     */
    public static void assertionIsFalse(boolean b,String msg,Object...params){
        if(b)throw new RuntimeException(String.format(msg,params));
    }
    /**
     * 断言结果为true    为false则报错
     * 可被Validate注解使用
     */
    public static void assertionIsTrue(boolean b,String msg,Object...params){
        if(!b)throw new RuntimeException(String.format(msg,params));
    }
    /**
     * 断言字符串是数字
     * 可被Validate注解使用
     */
    public static void assertionIsNumber(String str,String msg,Object...params){
        if(!Util.isNumeric(str))throw new RuntimeException(String.format(msg,params));
    }

    /**
     * 断言是邮箱
     * 可被Validate注解使用
     */
    public static void assertionIsUrl(String str,String msg,Object...params){
        if(!Util.isUrl(str))throw new RuntimeException(String.format(msg,params));
    }
    /**
     * 断言是邮箱
     * 可被Validate注解使用
     */
    public static void assertionIsEmail(String str,String msg,Object...params){
        if(!Util.isEmail(str))throw new RuntimeException(String.format(msg,params));
    }
    /**
     * 断言是手机号码
     * 可被Validate注解使用
     */
    public static void assertionIsPhone(String str,String msg,Object...params){
        if(!Util.isMobile(str))throw new RuntimeException(String.format(msg,params));
    }
    /**
     * 断言两个数字不相等 支持基本数据类型及其包装类和bigDecimal,bigInteger
     */
    public static void assertionIsNumberNotEq(Object num1,Object num2,String msg,Object...params){
        if(Util.isNull(num1)||Util.isNull(num2))throw new RuntimeException(String.format(msg,params));
        if(num1 instanceof BigDecimal ||num2 instanceof BigDecimal
                || num1 instanceof BigInteger || num2 instanceof BigInteger){
            if(new BigDecimal(num1.toString()).compareTo(new BigDecimal(num2.toString()))==0)throw new RuntimeException(String.format(msg,params));
        }else if(num1.equals(num2))throw new RuntimeException(String.format(msg,params));
    }
    /**
     * 断言两个数字相等 支持基本数据类型及其包装类和bigDecimal,bigInteger
     */
    public static void assertionIsNumberEq(Object num1,Object num2,String msg,Object...params){
        if(Util.isNull(num1)||Util.isNull(num2))throw new RuntimeException(String.format(msg,params));
        if(num1 instanceof BigDecimal||num2 instanceof BigDecimal
                || num1 instanceof BigInteger || num2 instanceof BigInteger){
            if(new BigDecimal(num1.toString()).compareTo(new BigDecimal(num2.toString()))!=0)throw new RuntimeException(String.format(msg,params));
        }else if(!num1.equals(num2))throw new RuntimeException(String.format(msg,params));
    }
    /**
     * 断言两个字符串相等  注意 两个字符串都不能为空 否则一样报错
     */
    public static void assertionIsStrEq(String str1,String str2,String msg,Object...params){
        if(Util.isNullOrEmpty(str1)||Util.isNullOrEmpty(str2)||!str1.equals(str2))throw new RuntimeException(String.format(msg,params));
    }

    /**
     * 断言两个字符串不能相等 一般用在修改密码时 新旧不密码不能相同
     */
    public static void assertionIsStrNotEq(String str1,String str2,String msg,Object...params){
        if(Util.isNullOrEmpty(str1)||Util.isNullOrEmpty(str2)||str1.equals(str2))throw new RuntimeException(String.format(msg,params));
    }
    /**
     * 断言为null
     * 可被Validate注解使用
     */
    public static void assertionIsNull(Object obj,String msg,Object...params){
        if(Util.isNotNull(obj))throw new RuntimeException(String.format(msg,params));
    }
    /**
     * 断言不为null
     * 可被Validate注解使用
     */
    public static void assertionIsNotNull(Object obj,String msg,Object...params){
        if(Util.isNull(obj))throw new RuntimeException(String.format(msg,params));
    }
    /**
     * 断言非空
     * 可被Validate注解使用
     */
    public static void assertionIsNotNullOrEmpty(String str,String msg,Object...params){
        if(Util.isNullOrEmpty(str))throw new RuntimeException(String.format(msg,params));
    }
    /**
     * 断言字符串是否是身份证
     * 可被Validate注解使用
     */
    public static void assertionIsIdCard(String idCard,String msg,Object...params){
        if(!Util.isCard(idCard))throw new RuntimeException(String.format(msg,params));
    }

    /**
     * 断言集合为空
     * 可被Validate注解使用
     */
    public static void assertionIsEmptyCollection(Collection collection, String msg, Object...params) {
        if (collection!=null && !collection.isEmpty())throw new RuntimeException(String.format(msg,params));
    }

    /**
     * 断言集合不为空
     * 可被Validate注解使用
     */
    public static void assertionIsNotEmptyCollection(Collection collection, String msg,Object...params) {
        if (collection==null || collection.isEmpty())throw new RuntimeException(String.format(msg,params));
    }
    /**
     * 断言代码不会报错
     * 可被Validate注解使用
     */
    public static void assertionNoException(NoReturnCallBack fn, String msg, Object...params){
        try{
            fn.callback();
        }catch (Exception e){
            throw new RuntimeException(String.format(msg,params));
        }
    }
    /**
     * 断言代码不会报错
     * 可被Validate注解使用
     */
    public static Object assertionNoException(CallBack fn, String msg, Object...params){
        try{
            return fn.callback();
        }catch (Exception e){
            throw new RuntimeException(String.format(msg,params));
        }
    }
    public static void assertionIsRangeLength(String str,int minLength,int maxLength,String msg,Object...params){
        if(!Util.isRangeLength(str,minLength,maxLength)){
            throw new RuntimeException(String.format(msg,params));
        }
    }

    /**
     * 断言字符串为车牌号码
     */
    public static void assertionIsCarNum(String str,String msg,Object...params){
        if(!Util.isCarNum(str))throw new RuntimeException(String.format(msg,params));
    }
    /**
     * 断言包含中文 不包含中文报错
     */
    public static void assertionIsContainChinese(String str,String msg,Object...params){
        if(!Util.isContainChinese(str))throw new RuntimeException(String.format(msg,params));
    }
    /**
     * 断言不包含中文 包含中文报错
     */
    public static void assertionIsNotContainChinese(String str,String msg,Object...params){
        if(Util.isContainChinese(str))throw new RuntimeException(String.format(msg,params));
    }
    /**
     * 断言字符串仅能为字母+数字 不能有特殊符号
     */
    public static void assertionIsOnlyStrAndNum(String str,String msg,Object...params){
        if(!Util.isOnlyStrAndNum(str))throw new RuntimeException(String.format(msg,params));
    }
    /**
     * 断言字符串包含字母+数字
     */
    public static void assertionIsStrAndNum(String str,String msg,Object...params){
        if(!Util.isStrAndNum(str))throw new RuntimeException(String.format(msg,params));
    }
    /**
     * 断言字符串有且必有大写小写字母
     */
    public static void assertionIsUpLow(String str,String msg,Object...params){
        if(!Util.isUpLow(str))throw new RuntimeException(String.format(msg,params));
    }
    /**
     * 断言字符串有且必有大写小写字母+数字
     */
    public static void assertionIsUpLowNum(String str,String msg,Object...params){
        if(!Util.isUpLowNum(str))throw new RuntimeException(String.format(msg,params));
    }
    /**
     * 断言字符串有且必有大写小写字母+数字+特殊字符
     */
    public static void assertionIsUpLowNumChar(String str,String msg,Object...params){
        if(!Util.isUpLowNumChar(str))throw new RuntimeException(String.format(msg,params));
    }
    /**
     * 断言字符串有且必有大写小写字母+数字 且首字母大写
     */
    public static void assertionIsUpOneAndLowNum(String str,String msg,Object...params){
        if(!Util.isUpOneAndLowNum(str))throw new RuntimeException(String.format(msg,params));
    }
    /**
     * 断言字符串有且必有大写小写字母+数字+特殊字符 且首字母大写
     */
    public static void assertionIsUpOneAndLowNumChar(String str,String msg,Object...params){
        if(!Util.isUpOneAndLowNumChar(str))throw new RuntimeException(String.format(msg,params));
    }
    /**
     * 断言值大于0 如果不是BigDecimal/BigInteger/Integer/Double/Float/Byte 直接断言失败
     */
    public static void assertionIsGtZero(Object value,String msg,Object...params){
        if(!Util.isGtZero(value))throw new RuntimeException(String.format(msg,params));
    }
    /**
     * 断言值最大为值 小于等于max
     */
    public static void assertionIsMax(Object value,Object max,String msg,Object...params){
        if(!Util.isMax(value,max))throw new RuntimeException(String.format(msg,params));
    }
    /**
     * 断言值最大为值 大于等于min
     */
    public static void assertionIsMin(Object value,Object min,String msg,Object...params){
        if(!Util.isMin(value,min))throw new RuntimeException(String.format(msg,params));
    }
}
