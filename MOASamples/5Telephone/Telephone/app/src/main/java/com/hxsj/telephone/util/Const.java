package com.hxsj.telephone.util;

import java.io.File;

import android.R.string;
import android.os.Environment;

/**
 * 存放一些activity用到的常量值
 * **/
public class Const {
	public static final String ACCOUNT = "account";
	// 传递选择标识
	public static final int SELECT_CONTACT_TYPE = 8;
	public static final int ORGANZE_CHOOSE=32;
	public static final String SELECT_CONTACT = "select_contact";
	public static final String PERSON_NAME = "username";
	public static final String PERSON_ID = "personid";

	//执行人类型，0：默认不可选择; 1 多选; 2 单选且不能选择自己
	public static final String EXECUTER_TYPE="executer_type";
	//判断哪里点击进入通讯录1:从菜单进入;0:从其他地方进入
	public static final String CONTACTS_FROM="contacts_from";
	public static final String MCHOOSE="choose_list";

	public static final String TYPE = "type";
//	public static final String COMMON_CONTACT="com.huniversity.net.util.common.contact";
public static final String APPRTC_CANCEL="apprtc.observier.cancel";
	public static final String  APPRTC_STATE="apprtc.observier.connect";
}
