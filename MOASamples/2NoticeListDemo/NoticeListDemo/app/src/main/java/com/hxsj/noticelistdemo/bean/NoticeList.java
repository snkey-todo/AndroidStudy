package com.hxsj.noticelistdemo.bean;

import java.util.List;

public class NoticeList {
	private int has_next_page;  //判断是否还有下一页的数据
	private List<Notice> list;   //通知列表数据集合

	public int getHas_next_page() {
		return has_next_page;
	}

	public void setHas_next_page(int has_next_page) {
		this.has_next_page = has_next_page;
	}

	public List<Notice> getList() {
		return list;
	}

	public void setList(List<Notice> list) {
		this.list = list;
	}

}
