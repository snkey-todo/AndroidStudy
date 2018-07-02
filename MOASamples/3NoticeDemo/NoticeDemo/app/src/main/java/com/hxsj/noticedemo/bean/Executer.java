package com.hxsj.noticedemo.bean;

import java.io.Serializable;

public class Executer implements Serializable{

	private String executer_id;
	private String executer_name;
	private String executer_login_id;

	public String getExecuter_id() {
		return executer_id;
	}

	public void setExecuter_id(String executer_id) {
		this.executer_id = executer_id;
	}

	public String getExecuter_name() {
		return executer_name;
	}

	public void setExecuter_name(String executer_name) {
		this.executer_name = executer_name;
	}

	public String getExecuter_login_id() {
		return executer_login_id;
	}

	public void setExecuter_login_id(String executer_login_id) {
		this.executer_login_id = executer_login_id;
	}

}
