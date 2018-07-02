package com.example.taskdemo.bean;

import java.util.List;

/*
 * 
 * */
public class TasksData {
	private int has_next_page;
	private List<TaskItem> list;

	public int getHas_next_page() {
		return has_next_page;
	}

	public void setHas_next_page(int has_next_page) {
		this.has_next_page = has_next_page;
	}

	public List<TaskItem> getList() {
		return list;
	}

	public void setList(List<TaskItem> list) {
		this.list = list;
	}

}
