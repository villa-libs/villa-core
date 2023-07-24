package com.villa.dto;

import java.io.Serializable;

public class ResultDTO<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private int code;//200或其他 200是正确/成功
	private String msg;//操作信息
	private T data;//查询单个获取的信息
	public static final String SUCCESS = "SUCCESS";
	public static ResultDTO putSuccess(String msg,Object data) {
		ResultDTO dto = new ResultDTO();
		dto.code = 200;
		dto.msg = msg;
		dto.data = data;
		return dto;
	}
	public static ResultDTO putSuccess(String msg) {
		return putSuccess(msg,null);
	}
	public static ResultDTO putSuccess(Object data) {
		return putSuccess(SUCCESS,data);
	}
	public static ResultDTO putSuccess() {
		return putSuccess(SUCCESS,null);
	}
	public static ResultDTO put401(String msg) {
		return putError(msg,401);
	}
	public static ResultDTO put500(String msg) {
		return putError(msg,500);
	}
	public static ResultDTO putError(String msg) {
		ResultDTO dto = new ResultDTO();
		dto.code = 500;
		dto.msg = msg;
		return dto;
	}
	public static ResultDTO putError(String msg,int code) {
		ResultDTO dto = new ResultDTO();
		dto.code = code;
		dto.msg = msg;
		return dto;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {return msg;}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
}
