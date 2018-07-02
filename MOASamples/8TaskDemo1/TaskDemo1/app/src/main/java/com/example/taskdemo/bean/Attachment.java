package com.example.taskdemo.bean;

public class Attachment {
	private String attach_name;
	private int attach_type;
	private String attach_url;
	private String attach_suffix;
	private int source_type;
	
	public String getAttach_name() {
		return attach_name;
	}

	public void setAttach_name(String attach_name) {
		this.attach_name = attach_name;
	}

	public int getAttach_type() {
		return attach_type;
	}

	public void setAttach_type(int attach_type) {
		this.attach_type = attach_type;
	}

	public String getAttach_url() {
		return attach_url;
	}

	public void setAttach_url(String attach_url) {
		this.attach_url = attach_url;
	}

	public int getSource_type() {
		return source_type;
	}

	public void setSource_type(int source_type) {
		this.source_type = source_type;
	}

	public String getAttach_suffix() {
		return attach_suffix;
	}

	public void setAttach_suffix(String attach_suffix) {
		this.attach_suffix = attach_suffix;
	}

}
