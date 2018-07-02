package com.hxsl.logindemo.log;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.NotificationManager;
import android.content.Context;

public class CrashHandler implements UncaughtExceptionHandler {

	// 需求是 整个应用程序 只有一个 MyCrash-Handler
	private static CrashHandler INSTANCE;
	private Context context;

	// 1.私有化构造方法
	private CrashHandler() {

	}

	public static synchronized CrashHandler getInstance() {
		if (INSTANCE == null)
			INSTANCE = new CrashHandler();
		return INSTANCE;
	}

	public void init(Context context) {
		this.context = context;
	}

	public void uncaughtException(Thread arg0, Throwable arg1) {
		Logger.getLogger().e(
				"[Thread Name: " + arg0.toString() + "]" + "Throwable : "
						+ arg1.getMessage());
		Logger.getLogger().e("application  is crash");

		// 统计
		// Qos.getInstance().set_exception("[Thread Name: " + arg0.toString() +
		// "]" + "Throwable : " + arg1.getMessage());

		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
		// Util.deleteAllData(false);
		// 干掉当前的程序
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}