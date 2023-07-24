package com.villa.comm;

/**
 * 通过这个接口  可以往方法中传入一个语句 类似回调函数
 * @bbs_url https://blog.csdn.net/u012169821
 */
public interface CallBack {
    Object callback() throws Exception;
}
