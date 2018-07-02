package com.hxsj.telephone.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/5/29.
 */
public class MsgType implements Serializable {
	public static final String SEND_CALL = "send_call"; //发送唤起接听
	public static final String ANSWER = "answer";// 执行接听操作
	public static final String BE_RESUE = "be_resue";// 被叫方拒绝接听
	public static final String RESUE = "resue";// 自己主动挂断
	public static final String CALL = "call";  //呼叫
	public static final String CONNECTING = "connecting"; //连接指令
	public static final String CANCEL = "cancel";  //取消指令
	public static final String AUDIO_ENABLED = "audio_enabled"; // 设置静音
	private String type;  //语音通话类型
	private String userid; //用户id
	private String username; //用户名称
	private boolean enabled; //是否设置静音

	public MsgType() {

	}

	public MsgType(String type, boolean enabled) {
		this.type = type;
		this.enabled = enabled;
	}

	public MsgType(String type, String userid) {
		this.type = type;
		this.userid = userid;
	}

	public MsgType(String type, String userid, String username) {
		this.type = type;
		this.userid = userid;
		this.username = username;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUserid() {
		return userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
