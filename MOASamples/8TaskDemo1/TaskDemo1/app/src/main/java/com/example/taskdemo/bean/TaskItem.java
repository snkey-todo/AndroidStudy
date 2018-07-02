package com.example.taskdemo.bean;

import java.util.ArrayList;

public class TaskItem {
	private String task_id;
	private String sender_user_id;
	private String sender_user_name;
	private String title;
	private String content;
	private String start_date;
	private String complete_date;
	private String created_time;
	private String updated_time;
	private String delay_date;
	private int status;
	private int has_attachment;
	private ArrayList<Executer> task_executer_list;
	private ArrayList<Receiver> task_receiver_list;
	private ArrayList<Attachment> attachment_list;
	private int task_total_day;
	private int task_finish_day;
	private String sender_head_img ;
	
	public String getCreated_time() {
		return created_time;
	}

	public void setCreated_time(String created_time) {
		this.created_time = created_time;
	}

	public String getUpdated_time() {
		return updated_time;
	}

	public void setUpdated_time(String updated_time) {
		this.updated_time = updated_time;
	}

	public String getDelay_date() {
		return delay_date;
	}

	public void setDelay_date(String delay_date) {
		this.delay_date = delay_date;
	}

	public String getTask_id() {
		return task_id;
	}

	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}

	public String getSender_user_id() {
		return sender_user_id;
	}

	public void setSender_user_id(String sender_user_id) {
		this.sender_user_id = sender_user_id;
	}

	public String getSender_user_name() {
		return sender_user_name;
	}

	public void setSender_user_name(String sender_user_name) {
		this.sender_user_name = sender_user_name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getComplete_date() {
		return complete_date;
	}

	public void setComplete_date(String complete_date) {
		this.complete_date = complete_date;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getHas_attachment() {
		return has_attachment;
	}

	public void setHas_attachment(int has_attachment) {
		this.has_attachment = has_attachment;
	}

	public ArrayList<Executer> getTask_executer_list() {
		return task_executer_list;
	}

	public void setTask_executer_list(ArrayList<Executer> task_executer_list) {
		this.task_executer_list = task_executer_list;
	}

	public ArrayList<Receiver> getTask_receiver_list() {
		return task_receiver_list;
	}

	public void setTask_receiver_list(ArrayList<Receiver> task_receiver_list) {
		this.task_receiver_list = task_receiver_list;
	}

	

	public ArrayList<Attachment> getAttachment_list() {
		return attachment_list;
	}

	public void setAttachment_list(ArrayList<Attachment> attachment_list) {
		this.attachment_list = attachment_list;
	}

	public int getTask_total_day() {
		return task_total_day;
	}

	public void setTask_total_day(int task_total_day) {
		this.task_total_day = task_total_day;
	}

	public int getTask_finish_day() {
		return task_finish_day;
	}

	public void setTask_finish_day(int task_finish_day) {
		this.task_finish_day = task_finish_day;
	}



	private String model;

	public TaskItem(String model) {
		this.model = model;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getSender_head_img() {
		return sender_head_img;
	}

	public void setSender_head_img(String sender_head_img) {
		this.sender_head_img = sender_head_img;
	}

}
