package com.hxsj.telephone;

import java.util.ArrayList;
import java.util.List;

import com.hxsj.telephone.util.AppUtils;
import com.hxsj.telephone.util.SPUtils;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;

public class AppLoader extends Application {
	
	public static int versionCode;
	public static String packageName;
	private static AppLoader mApplication;

	private List<Activity> activities = new ArrayList<Activity>();

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = this;
		versionCode = AppUtils.getVersionCode(getApplicationContext());

	}
	
	/**
	 * 获取apploader实例
	 * **/
	public synchronized static AppLoader getInstance() {
		return mApplication;
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
