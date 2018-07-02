package com.hxsl.contactsdemo.bean;

import java.util.List;

public class ContactUserData {

	private int has_next_page;
	private List<ContactUserInfo> list;

	public int getHas_next_page() {
		return has_next_page;
	}

	public void setHas_next_page(int has_next_page) {
		this.has_next_page = has_next_page;
	}

	public List<ContactUserInfo> getList() {
		return list;
	}

	public void setList(List<ContactUserInfo> list) {
		this.list = list;
	}

}
