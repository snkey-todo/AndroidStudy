package com.hxsl.contactsdemo;

import java.util.ArrayList;
import java.util.List;

import com.hxsl.contactsdemo.bean.UserInfo;
import com.hxsl.contactsdemo.util.AppUtils;
import com.hxsl.contactsdemo.util.SPUtils;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;

public class AppLoader extends Application {
	
	public static int versionCode;
	public static String packageName;
	private static AppLoader mApplication;
	private UserInfo mUserInfo;
	private List<Activity> activities = new ArrayList<Activity>();

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = this;
		versionCode = AppUtils.getVersionCode(getApplicationContext());
		initUserino();
	}
	
	/**
	 * 获取apploader实例
	 * **/
	public synchronized static AppLoader getInstance() {
		return mApplication;
	}
	
	/**
	 * 初始化用户信息
	 * **/
	private void initUserino() {
		String userid = (String) SPUtils.get(this, UserInfo.USER_ID, "");
		if (!TextUtils.isEmpty(userid)) {
			UserInfo info = new UserInfo();
			info.setCard_no((String) SPUtils.get(this, UserInfo.CARD_NO, ""));
			info.setEmail((String) SPUtils.get(this, UserInfo.EMAIL, ""));
			info.setHead_img((String) SPUtils.get(this, UserInfo.HEAD_IMG, ""));
			info.setLogin_id((String) SPUtils.get(this, UserInfo.LOGIN_ID, ""));
			info.setLogin_pwd((String) SPUtils.get(this, UserInfo.LOGIN_PWD, ""));
			info.setMobile_phone((String) SPUtils.get(this, UserInfo.MOBILE_PHONE, ""));
			info.setNick_name((String) SPUtils.get(this, UserInfo.NICK_NAME, ""));
			info.setSex((Integer) SPUtils.get(this, UserInfo.SEX, 0));
			info.setSign_img((String) SPUtils.get(this, UserInfo.SIGN_IMG, ""));
			info.setStatus((Integer) SPUtils.get(this, UserInfo.STATUES, 0));
			info.setTelephone(String.valueOf(SPUtils.get(this, UserInfo.TELEPHONE, "")));
			info.setTrue_name((String) SPUtils.get(this, UserInfo.TRUE_NAME, ""));
			info.setUser_id(userid);
			info.setUser_type((Integer) SPUtils.get(this, UserInfo.USER_TYPE, 0));
			this.mUserInfo = info;
		}
	}
	
	public UserInfo getmUserInfo() {
		return mUserInfo;
	}

	public void setmUserInfo(UserInfo mUserInfo) {
		this.mUserInfo = mUserInfo;
	}
	
	/**
	 * activity跳转索引
	 * **/
	public void addActivity(Activity activity) {
		activities.add(activity);
	}
	
	/**
	 * 结束指定的Activity
	 * 
	 * @param activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			this.activities.remove(activity);
			activity.finish();
			activity = null;
		}
	}

}
