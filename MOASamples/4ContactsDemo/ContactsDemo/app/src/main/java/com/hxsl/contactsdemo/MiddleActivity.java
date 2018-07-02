package com.hxsl.contactsdemo;

import com.hxsl.contactsdemo.util.Const;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

@ContentView(R.layout.activity_task)
public class MiddleActivity extends Activity {
	
	@ViewInject(R.id.tv_public_title)
	private TextView title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ViewUtils.inject(this);
		title.setText("通讯录测试");
	}
	
	@OnClick(R.id.button1)
	private void onButtonClick(View view){
		Intent intent = new Intent();
		intent.setClass(this, ContactsActivity.class);
		intent.putExtra(Const.CONTACTS_FROM, 1);
		startActivity(intent);
	}
	
	@OnClick(R.id.iv_public_back)
	private void onBackclick(View view){
		finish();
	}
	
}
