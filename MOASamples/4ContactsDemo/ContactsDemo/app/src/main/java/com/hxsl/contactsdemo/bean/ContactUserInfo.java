package com.hxsl.contactsdemo.bean;

import java.io.Serializable;

/*
 * 联系人列表
 * */
public class ContactUserInfo implements Serializable, Comparable<ContactUserInfo> {

	
	private String login_id = "";
	private int sex;
	private String pingyin; // 全拼
	public boolean mSelect = false;

	private String id;
	private String name;
	private String avatar;
	private String initial;
	private String mobile_phone;
	private String email;
	private String department;
	private String nick_name;
	private int is_collect;

	public String getPingyin() {
		return pingyin;
	}

	public void setPingyin(String pingyin) {
		this.pingyin = pingyin;
	}

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

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getInitial() {
		return initial;
	}

	public void setInitial(String initial) {
		this.initial = initial;
	}

	public String getMobile_phone() {
		return mobile_phone;
	}

	public void setMobile_phone(String mobile_phone) {
		this.mobile_phone = mobile_phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	@Override
	public int compareTo(ContactUserInfo another) {

		return pingyin.compareToIgnoreCase(another.getPingyin());
	}	

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getLogin_id() {
		return login_id;
	}

	public void setLogin_id(String login_id) {
		this.login_id = login_id;
	}

	public int getIs_collect() {
		return is_collect;
	}

	public void setIs_collect(int is_collect) {
		this.is_collect = is_collect;
	}
	

}
