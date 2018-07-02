package com.hxsj.noticelistdemo.bean;

/**
 * 公告通知
 * **/
public class Notice {
	private String notice_id;  //通知id
	private String u_name;   //通知发出人
	private String title;    //通知标题
	private String content;  //通知内容
	private String created_time; //通知创建时间
	private String url;  //通知url地址，扩展字段

	public String getNotice_id() {
		return notice_id;
	}

	public void setNotice_id(String notice_id) {
		this.notice_id = notice_id;
	}

	public String getU_name() {
		return u_name;
	}

	public void setU_name(String u_name) {
		this.u_name = u_name;
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

	public String getCreated_time() {
		return created_time;
	}

	public void setCreated_time(String created_time) {
		this.created_time = created_time;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
