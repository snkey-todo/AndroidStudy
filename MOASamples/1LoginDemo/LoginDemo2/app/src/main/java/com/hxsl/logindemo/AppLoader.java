package com.hxsl.logindemo;



import com.hxsl.logindemo.util.AppUtils;

import android.app.Application;

public class AppLoader extends Application {
	public static int versionCode;
	@Override
	public void onCreate() {
		super.onCreate();
		versionCode = AppUtils.getVersionCode(getApplicationContext());
	}

}
