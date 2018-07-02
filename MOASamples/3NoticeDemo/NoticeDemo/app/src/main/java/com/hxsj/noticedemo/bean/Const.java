package com.hxsj.noticedemo.bean;

/**
 * 存放一些activity用到的常量值
 * **/
public class Const {
	public static final String ACCOUNT = "account";
	// 分页请求数据极限值
	public static final int PAGE_LIMIT = 20;
	// 传递选择标识
	public static final int SELECT_CONTACT_TYPE = 8;
	public static final int ORGANZE_CHOOSE=32;
	public static final String SELECT_CONTACT = "select_contact";
	//执行人类型，0：默认不可选择; 1 多选; 2 单选且不能选择自己
	public static final String EXECUTER_TYPE="executer_type";
	//判断哪里点击进入通讯录1:从菜单进入;0:从其他地方进入
	public static final String CONTACTS_FROM="contacts_from";
	public static final String MCHOOSE="choose_list";

	// 取图片
	public static final int REQUEST_PICK = 5;
	public static final int TAG_EXCUTORE = 10;
	//图片上传数量限制
	public static final int FILE_LIMIT=10;

}
