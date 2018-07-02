package com.hxsj.noticedemo.bean;

public class UserInfo {
	public static final String USER_ID = "user_id";
	public static final String USER_TYPE = "user_type";
	public static final String LOGIN_ID = "login_id";
	public static final String LOGIN_PWD = "login_pwd";
	public static final String CARD_NO = "card_no";
	public static final String TRUE_NAME = "true_name";
	public static final String NICK_NAME = "nick_name";
	public static final String MOBILE_PHONE = "mobile_phone";
	public static final String TELEPHONE = "telephone";
	public static final String SEX = "sex";
	public static final String EMAIL = "email";
	public static final String STATUES = "statues";

	public static final String HEAD_IMG = "head_img";
	public static final String SIGN_IMG = "sign_img";

	public static final String NOTICE_ID = "notice_id";
	public static final String MODIFY_PWD="modify_pwd";
	public static final String PERMISSIONS="permissions";

	private String user_id;
	private int user_type;
	private String login_id;
	private String login_pwd;
	private String card_no;
	private String true_name;
	private String nick_name;

	private String mobile_phone;
	private String telephone;
	private int sex;
	private String email;
	private int status;
	private String head_img;
	private String sign_img;
	private int is_modify_pwd;

	private int permissions;
	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public int getUser_type() {
		return user_type;
	}

	public void setUser_type(int user_type) {
		this.user_type = user_type;
	}

	public String getLogin_id() {
		return login_id;
	}

	public void setLogin_id(String login_id) {
		this.login_id = login_id;
	}

	public String getLogin_pwd() {
		return login_pwd;
	}

	public void setLogin_pwd(String login_pwd) {
		this.login_pwd = login_pwd;
	}

	public String getCard_no() {
		return card_no;
	}

	public void setCard_no(String card_no) {
		this.card_no = card_no;
	}

	public String getTrue_name() {
		return true_name;
	}

	public void setTrue_name(String true_name) {
		this.true_name = true_name;
	}

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	public String getMobile_phone() {
		return mobile_phone;
	}

	public void setMobile_phone(String mobile_phone) {
		this.mobile_phone = mobile_phone;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getHead_img() {
		return head_img;
	}

	public void setHead_img(String head_img) {
		this.head_img = head_img;
	}

	public String getSign_img() {
		return sign_img;
	}

	public void setSign_img(String sign_img) {
		this.sign_img = sign_img;
	}

	public int getIs_modify_pwd() {
		return is_modify_pwd;
	}

	public void setIs_modify_pwd(int is_modify_pwd) {
		this.is_modify_pwd = is_modify_pwd;
	}

	public int getPermissions() {
		return permissions;
	}

	public void setPermissions(int permissions) {
		this.permissions = permissions;
	}

}
