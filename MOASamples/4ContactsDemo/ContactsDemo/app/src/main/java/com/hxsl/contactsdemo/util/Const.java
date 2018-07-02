package com.hxsl.contactsdemo.util;

import java.io.File;

import android.R.string;
import android.os.Environment;

/**
 * 存放一些activity用到的常量值
 * **/
public class Const {
	public static final String ACCOUNT = "account";
//	public static final String PASSWORLD = "password";
//	public static final String NEW_NOTICE="com.huniversity.net.notice";
//	public static String CITY_DB_PATH = "/data/data/com.huniversity.net/databases/";
//	// 分页请求数据极限值
//	public static final int PAGE_LIMIT = 20;
//	//图片上传数量限制 
//	public static final int FILE_LIMIT=10;
//	// 普通审批
//	public static final int APPROVE_NORMAL = 0;
//	// 请假单
//	public static final int APPROVE_REQUEST = 1;
//	// 报销单
//	public static final int APPROVE_EXPENSE = 2;
//	// 差旅单
//	public static final int APPROVE_TRAVEL = 3;
//	// 借款单
//	public static final int APPROVE_BILL = 4;
//
//	// 取图片
//	public static final int REQUEST_PICK = 5;
//
//	// 添加标题标识
//	public static final int ADD_TITLE_TYPE = 6;
//	// 添加内容标识
//	public static final int ADD_CONTENT_TYPE = 7;
	
	// 传递选择标识
	public static final int SELECT_CONTACT_TYPE = 8;
	
//	public static final int TAG_SENDER = 9;
//	public static final int TAG_EXCUTORE = 10;
//	public final static int ADD_MESSAGE = 11;
//	public final static int ADD_GROUP_MESSAGE = 12;
//
//	public final static int CREAT_GROUP_TYPE = 13;
//	public final static int MESSAGE_CHAT = 14;
//	public final static int MESSAGE_GROUPCHAT = 15;
//	public final static int MESSAGE_NOTIFICATION=151;
//	
//	public final static int BORAD_TYPE = 16;
//	
//	//任务详细
//	public static final int TASK_FAVROITE = 17;
//	public static final int TASK_DELAY = 18;
//	public static final int TASK_COMPLETE = 19;
//	public static final int TASK_OPERATOR = 190;
//	public static final int TASK_ADDOPERATOR = 191;
//	public static final int TASK_ADDRECEIVER = 192;
//	public static final int TASK_CANCEL = 193;
//	public static final int TASK_REPORT=20;
//	public final static int APPROVAL_REQUEST=21;
//	
//	public static final int CHANNEL_TYPE=22;
//	//通知接收人列表和增加接收人
//	public static final int NOTICE_ADD=23;
//	public static final int NOTICE_RECEIVER=24;
//	
//	
//	//审批接口
//	public static final int APPROVAL_RESASON=23;
//	public static final int APPROVAL_BUNDGET=24;
//	public static final int APPROVAL_ADVANCEFEE=25;
//	public static final int APPROVAL_DAYS=26;
//	public static final int APPROVAL_STARTDEST=27;
//	public static final int APPROVAL_ENDDEST=28;
//	public static final int APPROVAL_STARTTIME=29;
//	public static final int APPROVAL_ENDTIME=30;
//	public static final int APPROVAL_TRAFFIC=31;
	public static final int ORGANZE_CHOOSE=32;
//	
//	public static final int REPORT_DAY=33;
//	public static final int REPORT_WEEK=34;
//	public static final int REPORT_MONTH=35;
//	// 用户角色的标识
//	public static final String USER_ROLE = "user_role";
//	// 添加内容标题
//	public static final String ADD_TITLE = "add_title";
//	public static final String ADD_CONTENT = "add_content";
	public static final String SELECT_CONTACT = "select_contact";
//	public static final String COUSER_ID = "course_id";
//	public static final String LIVE_INTRODUCE = "introduce";
//	public static final String LIVE_CHAPTER = "live_chapter";
//	public static final String LIVE_CATEGORY = "category";
//	public static final String IMAGE_SIZE = "imagesize";
//	public static final String CITY = "city";
//	public static final String TRANSPORT = "transport";
//	public static final String CREAT_GROUP = "creat_group";
//	public static final String MESSAGE_TYPE = "message_type";
//	public static final String BOARD_ID = "boardid";
//	public static final String BOARD_NAME = "boardname";
//
//	// 问答数据
//	public static final String PERSON_IMAGE = "avatar";
//	public static final String PERSON_NAME = "username";
//	public static final String QUESTION_TITLE = "question_title";
//	public static final String QUESTION_CONTENT = "question_content";
//	public static final String QUESTION_TIME = "question_time";
//	public static final String PERSON_ID = "personid";
//	public static final String QUESTION_ID = "question_id";
//	public static final String TASK_ID = "task_id";
//	public static final String TASK_TITLE = "task_title";
//	public static final String LIVE_URL="url";
//	public static final String TASK_EXECUTORE_ID="executor_id";
//	public static final String TASK_RECEVIER_ID="recevier_id";
//	
//	//图片地址
//	public final static String IMAGE_URL = "image_url";
//	//审批状态
//	public final static String APPROVAL_STATUES="approvel_status";
//	public static final String AUDIT_ID = "audit_id";
//	public static final String AUDIT_NAME = "audit_name";
//	public static final String CREATER_ID="creater_id";
//	
//	public static final String RECEVIER_PERSON="recevier_person";
//	
	//执行人类型，0：默认不可选择; 1 多选; 2 单选且不能选择自己
	public static final String EXECUTER_TYPE="executer_type";
//	public static final String PERSONLIST = "audit_person_list";
//	
//	public static final String CREATE_CHANNEL="channel";
//	public static final String PLAY_TYPE="play_type";
//	public static final String PLAY_URL="play_url";
//	
//	//图片浏览页面
//	public static final String EXTRA_URLS = "extra_urls";
//	public static final String EXTRA_POS = "EXTRA_POS";
//	public static final String EXTRA_UID = "extra_uid";
//	public static final String EXTRA_NICKNAME = "extra_nickname";
//	//我要汇报页面
//	public static final String SENDER_USER_ID = "sender_user_id";
//	public static final String SENDER_LOGIN_ID = "sender_login_id";
//	public static final String WEB_URL="url";
//	public static final String WEB_TYPE="webtype";
//	public static final String FILE_NAME="filename";
//	//控制引导页的显示与隐藏
//	public static final String FIRST_TIME="IsFirstTime";
//	public static final String TIME="time";
	
	//判断哪里点击进入通讯录1:从菜单进入;0:从其他地方进入
	public static final String CONTACTS_FROM="contacts_from";
	
//	//判断是否是任务的发起人
//	public static final String TASK_SENDER="task_sender";
//	public static final String ORGANIZE="organize";
	public static final String MCHOOSE="choose_list";
//	
//	//通知详情
//	public static final String NOTICE_ID="notice_id";
//	public static final String NOTICE_LIST="notice_list";
//	public static final String NOTICE_TIME="notice_time";
//	public static final String REPORT_ID="report_id";
//	public static final String REPORT_TYPE="report_type";
//	public static final String REPORT_U_ID="u_id";
//	
//	//boolean类型数据，任务，审批，汇报工作详情判断是否为发言
//	public static final String IS_SPEAK="is_speak";
//	
//	public static final String LOCATION="location";
//
//	
//	public static final String[] OPERATOR_FILE={ "图片", "文件","我的下载"};
//	public static final String FILE_DOWN=Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FileDownloader";
//	public static final String ATTENDE = "attende";
//	public static final String ROOM_ID = "room_id";
//	public static final String TITLE = "title";
//	public static final String TYPE = "type";
//	public static final String APPOINT_ID="appoint_id";
//	public static final String APPROVE[] = { "普通审批", "编外人员工资审批", "人员调配申请", "采购审批", "社会实践申报", "社团注册申请", "请假单", "报销单", "差旅单", "借款单" };
////	private static final int approverState[] = { 0, 101, 102, 103, 104,1,2,3,4,5 };
//	
//	public static final String APPROVE_TITLE="title";
//	public static final String APPRTC_CANCEL="apprtc.observier.cancel";
//	public static final String  APPRTC_STATE="apprtc.observier.connect";
//	public static final String SAVE_INFORMATION = "save_information";
//	public static final String COMMON_CONTACT="com.huniversity.net.util.common.contact";
	
}
