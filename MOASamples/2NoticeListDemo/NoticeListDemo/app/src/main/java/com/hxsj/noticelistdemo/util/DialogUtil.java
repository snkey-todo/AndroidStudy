package com.hxsj.noticelistdemo.util;



import com.hxsj.noticelistdemo.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class DialogUtil {
	public static Dialog getMenuDialog(Activity context, View view) {

		final Dialog dialog = new Dialog(context, R.style.MenuDialogStyle);
		dialog.setContentView(view);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();

		int screenW = getScreenWidth(context);
		// int screenH = getScreenHeight(context);
		lp.width = screenW;
		window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
		window.setWindowAnimations(R.style.MenuDialogAnimation); // 添加动画
		return dialog;
	}

	public static Dialog getLoginDialog(Activity context) {

		final Dialog dialog = new Dialog(context, R.style.DialogStyle);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.custom_progress_dialog);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();

		int screenW = getScreenWidth(context);
		lp.width = (int) (0.6 * screenW);

		TextView titleTxtv = (TextView) dialog.findViewById(R.id.dialogText);
		titleTxtv.setText(R.string.login_prompt);
		return dialog;
	}

//	/**
//	 * 说明：显示网络错误的对话框
//	 * 
//	 ***/
//	public static Dialog showNetworkErrorDialog(final Activity context, int title) {
//		CustomDialog dialog = new CustomDialog.Builder(context).setTitle(title).setMessage(R.string.network_error_msg)
//				.setPositiveButton(R.string.setting_network, new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int position) {
//						dialog.dismiss();
//						Intent wifiSetting = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
//						context.startActivity(wifiSetting);
//					}
//				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//
//					}
//				}).create();
//		// dialog.show();
//		return dialog;
//	}

//	public static void showDownloadDialog(final Context context,final String url,final String name) {
//		CustomDialog dialog = new CustomDialog.Builder(context).setTitle("下载提示").setMessage("确定下载该附件吗?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int position) {
//				dialog.dismiss();
//				Intent intent = new Intent();
//				intent.setClass(context, MyDocumentActivity.class);
//				intent.putExtra(Const.FILE_NAME,name);
//				intent.putExtra(Const.WEB_URL, url);
//				context.startActivity(intent);
//			}
//		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//
//			}
//		}).create();
//		 dialog.show();
////		return dialog;
//	}

	public static Dialog getprocessDialog(Activity context, String str) {

		final Dialog dialog = new Dialog(context, R.style.DialogStyle);
		dialog.setCancelable(true);

		dialog.setContentView(R.layout.custom_progress_dialog);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();

		int screenW = getScreenWidth(context);
		lp.width = (int) (0.6 * screenW);

		TextView titleTxtv = (TextView) dialog.findViewById(R.id.dialogText);
		titleTxtv.setText(str);
		return dialog;
	}

	public static int getScreenWidth(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	public static int getScreenHeight(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}
}