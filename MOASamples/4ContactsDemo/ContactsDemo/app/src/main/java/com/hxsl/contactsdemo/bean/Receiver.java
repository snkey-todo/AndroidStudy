package com.hxsl.contactsdemo.bean;

import java.io.Serializable;

public class Receiver implements Serializable {

	private String audit_receiver_id;
	private String receiver_id;
	private String receiver_name;
	private String receiver_head_img;

	public String getReceiver_id() {
		return receiver_id;
	}

	public void setReceiver_id(String receiver_id) {
		this.receiver_id = receiver_id;
	}

	public String getReceiver_name() {
		return receiver_name;
	}

	public void setReceiver_name(String receiver_name) {
		this.receiver_name = receiver_name;
	}

	public String getAudit_receiver_id() {
		return audit_receiver_id;
	}

	public void setAudit_receiver_id(String audit_receiver_id) {
		this.audit_receiver_id = audit_receiver_id;
	}

	public String getReceiver_head_img() {
		return receiver_head_img;
	}

	public void setReceiver_head_img(String receiver_head_img) {
		this.receiver_head_img = receiver_head_img;
	}

	

}
