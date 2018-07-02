package com.hxsj.telephone.util;


import com.hxsj.telephone.R;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtils {
	
	private static Toast createToast(Context context, CharSequence title, CharSequence text, int drawId){
		View view = LayoutInflater.from(context).inflate(R.layout.toast_dialog, null);
		ImageView imgView = (ImageView) view.findViewById(R.id.img_dialogIcon);
		TextView titleView = (TextView) view.findViewById(R.id.tv_dialogTitle);
		TextView messageView = (TextView) view.findViewById(R.id.tv_dialogMsg);
		if (drawId != 0) {
			imgView.setImageDrawable(context.getResources().getDrawable(drawId));
			imgView.setVisibility(View.VISIBLE);
		} else {
			imgView.setVisibility(View.GONE);
			MarginLayoutParams params = (MarginLayoutParams) titleView.getLayoutParams();
			params.topMargin = 0;
			titleView.setLayoutParams(params);
		}

		if (TextUtils.isEmpty(title)) {
			titleView.setVisibility(View.GONE);
			MarginLayoutParams params = (MarginLayoutParams) messageView.getLayoutParams();
			params.topMargin = 0;
			messageView.setLayoutParams(params);
		} else {
			titleView.setVisibility(View.VISIBLE);
			titleView.setText(title);
		}

		if (TextUtils.isEmpty(text)) {
			messageView.setVisibility(View.GONE);
		} else {
			messageView.setVisibility(View.VISIBLE);
			messageView.setText(text);
		}
		Toast toast = new Toast(context);
		toast.setView(view);
		toast.setDuration(Toast.LENGTH_SHORT);
		return toast;
	}

	public static void show(Context context, CharSequence text) {
		showCenter(context, text);
	}
	
	public static void show(Context context, int resId) {
		show(context, context.getText(resId));
	}
	
	public static void showBottom(Context context, CharSequence text) {
		if (null != context) {
			Toast toast = createToast(context, null, text , 0);
			toast.show();
		}
	}

	public static void showCenter(Context context, CharSequence text) {
		if (null != context) {
			showCenter(context, text, null, 0);
		}
	}

	public static void showCenter(Context context, CharSequence title, CharSequence text, int drawId) {
		if (null != context) {
			Toast toast = createToast(context, title, text, drawId);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}

	public static void showCenter(Context context, int strId, int drawId) {
		if (null != context) {
			showCenter(context, context.getText(strId), null, drawId);
		}
	}

	public static void showCenter(Context context, int titleId, int msgId, int drawId) {
		if (null != context) {
			showCenter(context, context.getText(titleId), context.getText(msgId), drawId);
		}
	}

}
