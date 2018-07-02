package com.hxsj.noticedemo;

import java.util.ArrayList;

import com.hxsj.noticedemo.bean.UserInfo;
import com.hxsj.noticedemo.util.SPUtils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;

/**
 * activity基类
 **/
public abstract class BaseActivity extends FragmentActivity {
	public static ArrayList<BackPressHandler> mListeners = new ArrayList<BackPressHandler>();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

//		AppLoader.getInstance().addActivity(this);
		// MessageObserver.getInstance().registerSingleObserver(invitationListener,
		// new ObserverFilter(Constants.ACTION_INVITATION_LISTENER));
	}

	public void saveData(UserInfo info) {
		SPUtils.put(this, UserInfo.CARD_NO, info.getCard_no());
		SPUtils.put(this, UserInfo.EMAIL, info.getEmail());
		SPUtils.put(this, UserInfo.HEAD_IMG, info.getHead_img());
		SPUtils.put(this, UserInfo.LOGIN_ID, info.getLogin_id());
		SPUtils.put(this, UserInfo.LOGIN_PWD, info.getLogin_pwd());
		SPUtils.put(this, UserInfo.MOBILE_PHONE, info.getMobile_phone());
		SPUtils.put(this, UserInfo.NICK_NAME, info.getNick_name());
		SPUtils.put(this, UserInfo.SEX, info.getSex());
		SPUtils.put(this, UserInfo.SIGN_IMG, info.getSign_img());
		SPUtils.put(this, UserInfo.STATUES, info.getStatus());
		SPUtils.put(this, UserInfo.TELEPHONE, info.getTelephone());
		SPUtils.put(this, UserInfo.TRUE_NAME, info.getTrue_name());
		SPUtils.put(this, UserInfo.USER_ID, info.getUser_id());
		SPUtils.put(this, UserInfo.USER_TYPE, info.getUser_type());
		SPUtils.put(this, UserInfo.PERMISSIONS, info.getPermissions());
		SPUtils.put(this, UserInfo.MODIFY_PWD, info.getIs_modify_pwd());
	}
	// Observer invitationListener = new Observer() {
	//
	// @Override
	// public void notifyChanged(Object object) {
	//
	// }
	// };
	//
	// private void showInvitationDialog(final String inviter, final String
	// room) {
	// final KXDialog dialog = new KXDialog(this);
	// dialog.setTitle("入群邀请");
	// dialog.setMessage(inviter + "邀请你进入群组");
	// dialog.setPositiveButton("进去", new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // SmackImpl impl = AppLoader.getInstance().getSmackImpl();
	// // impl.joinForm(inviter, room);
	// }
	// });
	// dialog.setNegativeButton("取消", new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	//
	// }
	// });
	// dialog.show();
	// }

	@TargetApi(19)
	private void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public static abstract interface BackPressHandler {

		public abstract void activityOnResume();

		public abstract void activityOnPause();

	}

	public void showDeleteDialog(int position) {
	}
}
