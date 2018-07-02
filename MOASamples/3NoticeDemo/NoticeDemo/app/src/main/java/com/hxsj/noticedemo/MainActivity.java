package com.hxsj.noticedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_main)
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

	}
	@OnClick(R.id.btn_notice)
	private void onNoticeClick(View v){
		Intent intent=new Intent();
		intent.setClass(MainActivity.this, NoticeActivity.class);
		startActivity(intent);
	}
	@OnClick(R.id.btn_new_notice)
	private void onNewNoticeClick(View v){
		Intent intent=new Intent();
		intent.setClass(this,NewNoticeActivity.class);
		startActivity(intent);
	}

	
}
