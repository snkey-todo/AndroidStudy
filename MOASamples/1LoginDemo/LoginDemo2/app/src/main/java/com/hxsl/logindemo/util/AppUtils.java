package com.hxsl.logindemo.util;

import java.util.List;
import java.util.UUID;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

//跟App相关的辅助类
public class AppUtils {

	private AppUtils() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");

	}

	/**
	 * 获取应用程序名称
	 */
	public static String getAppName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * [获取应用程序版本名称信息]
	 * 
	 * @param context
	 * @return 当前应用的版本名称
	 */
	public static String getVersionName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionName;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int getVersionCode(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}

	/**
	 * 读取手机串号即是手机imei号
	 * 
	 * @param con
	 *            上下文
	 * @return String 手机串号
	 */
	public static String readTelephoneSerialNum(Context con) {
		final TelephonyManager tm = (TelephonyManager) con.getSystemService(Context.TELEPHONY_SERVICE);

		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = "" + android.provider.Settings.Secure.getString(con.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String deviceId = deviceUuid.toString().trim().replace("-", "");
		return deviceId;
	}

	/**
	 * 判断应用是不是在前台运行
	 * **/
	public static boolean isAppOnForeground(Context context) {
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfos = mActivityManager.getRunningTasks(1);
		if (taskInfos.size() > 0 && TextUtils.equals(context.getPackageName(), taskInfos.get(0).topActivity.getPackageName())) {
			return true;
		}

//		List<RunningAppProcessInfo> appProcesses = mActivityManager.getRunningAppProcesses();
//		if (appProcesses == null)
//			return false;
//		for (RunningAppProcessInfo appProcess : appProcesses) {
//			// L.i("liweiping", appProcess.processName);
//			// The name of the process that this object is associated with.
//			if (appProcess.processName.equals(mPackageName) && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//				return true;
//			}
//		}
		return false;
	}
}