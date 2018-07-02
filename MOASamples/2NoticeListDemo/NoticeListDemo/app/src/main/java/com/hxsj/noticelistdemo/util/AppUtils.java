package com.hxsj.noticelistdemo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

		// List<RunningAppProcessInfo> appProcesses =
		// mActivityManager.getRunningAppProcesses();
		// if (appProcesses == null)
		// return false;
		// for (RunningAppProcessInfo appProcess : appProcesses) {
		// // L.i("liweiping", appProcess.processName);
		// // The name of the process that this object is associated with.
		// if (appProcess.processName.equals(mPackageName) &&
		// appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
		// {
		// return true;
		// }
		// }
		return false;
	}

	public static String getTimeLine(String time) {
		if (TextUtils.isEmpty(time)) {

			return "";
		}
		long current = System.currentTimeMillis() / 1000;
		long temp = current - getSecondsFromDate(time);
		String str = "";
		if (temp < 60 * 60) {
			if (temp / 60 == 0)
				str = "刚刚";
			else
				str = temp / 60 + "分钟前";
		} else if (temp < 24 * 60 * 60)
			str = temp / 60 / 60 + "小时前";
		else if (temp < 24 * 2 * 60 * 60)
			str = "昨天";
		else if (temp < 30 * 60 * 60 * 24)
			str = temp / 24 / 60 / 60 + "天前";
		else
			str = time.substring(0, "yyyy-MM-dd".length());
		return str;
	}

	public static long getSecondsFromDate(String expireDate) {
		if (expireDate == null || expireDate.trim().equals(""))
			return 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = sdf.parse(expireDate);
			return date.getTime() / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}
}