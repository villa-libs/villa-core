package com.villa.dto;

/**
 * @作者 微笑い一刀
 * @bbs_url https://blog.csdn.net/u012169821
 */
public interface ErrCodeDTO {
    /** 请求未授权(签名失效) Request not authorized */
    String ox00001 = "0x00001";
    /** 此资源需要登录,但未登录(未携带登录token) 或token验证失败 */
    String ox00002 = "0x00002";
    /** 系统异常 */
    String ox99999 = "0x99999";
    /** 上传失败 */
    String ox99998 = "0x99998";
}
