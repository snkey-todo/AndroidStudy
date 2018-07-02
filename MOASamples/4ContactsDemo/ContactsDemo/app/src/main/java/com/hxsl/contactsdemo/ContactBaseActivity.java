package com.hxsl.contactsdemo;

import com.hxsl.contactsdemo.bean.ContactUserInfo;

import android.os.Bundle;

public abstract class ContactBaseActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// MessageObserver.getInstance().unregisterObserver(observer);
	}

	public void enterPersonDetail(int position) {

	}


	public void updateUserInfo(ContactUserInfo contactUserInfo) {

	}

	public void choosePerson(int position) {
	}
}
