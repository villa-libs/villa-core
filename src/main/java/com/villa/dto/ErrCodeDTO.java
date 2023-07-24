package com.villa.dto;

public interface ErrCodeDTO {
    /** 请求未授权(签名失效) Request not authorized */
    String ox00001 = "0x00001";
    /** 此资源需要登录,但未登录(未携带登录token) 或token验证失败 */
    String ox00002 = "0x00002";
    //-------------------------------邮箱/手机验证码相关---------------------------------
    /** 验证码错误 */
    String ox00101 = "0x00101";
    /** 发送目标不能为空 */
    String ox00102 = "0x00102";
    /** 发送太频繁 比如设置60秒一条 在60秒发送多条会报此错 */
    String ox00103 = "0x00103";
    /** 发送目标发送数量超限 */
    String ox00104 = "0x00104";
    /** IP超出发送上限 */
    String ox00105 = "0x00105";

    /** 系统异常 */
    String ox99999 = "0x99999";
    /** 上传失败 */
    String ox99998 = "0x99998";
}
