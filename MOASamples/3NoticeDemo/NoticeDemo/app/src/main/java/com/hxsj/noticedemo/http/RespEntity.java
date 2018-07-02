package com.hxsj.noticedemo.http;

public class RespEntity<T> {
	private int code;
	private String msg;
	private T data;
	private int is_encrypt = 0;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public int getIs_encrypt() {
		return is_encrypt;
	}

	public void setIs_encrypt(int is_encrypt) {
		this.is_encrypt = is_encrypt;
	}

}
