package com.hxsj.noticedemo.bean;

public class Contact {

	private String id;
	private String name;
	private String code;
	private int depth;
	private int count;
	private boolean allow_next;
	private boolean is_last;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public boolean getAllow_next() {
		return allow_next;
	}

	public void setAllow_next(boolean allow_next) {
		this.allow_next = allow_next;
	}

	public boolean getIs_last() {
		return is_last;
	}

	public void setIs_last(boolean is_last) {
		this.is_last = is_last;
	}

}
