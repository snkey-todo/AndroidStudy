package com.hxsl.logindemo;

import com.hxsl.logindemo.bean.UserInfo;
import com.hxsl.logindemo.http.ParamUtils;
import com.hxsl.logindemo.http.Parser;
import com.hxsl.logindemo.http.RespEntity;
import com.hxsl.logindemo.http.UrlUtils;
import com.hxsl.logindemo.util.AppUtils;
import com.hxsl.logindemo.util.ToastUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

@ContentView(R.layout.activity_main)
public class LoginActivity extends Activity {
	@ViewInject(R.id.et_login_account_number)
	private EditText mEtAccountNumber;  //初始化登录账号输入框
	@ViewInject(R.id.et_login_password)
	private EditText mEtPassword; //初始化密码输入框
	@ViewInject(R.id.tv_login)
	private TextView mLogin; //初始化登录按钮
	@ViewInject(R.id.tv_login_version)
	private TextView mVersionTextView;
	@ViewInject(R.id.tv_login_teacher)
	private TextView mTeacherChoose;
	@ViewInject(R.id.tv_login_stu)
	private TextView mStuChoose;
//	private Dialog mLoginDialog;

	//获取输入的账号
	private String mAccount;
	//获取输入的密码
	private String mPassword;

	private int role;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		ViewUtils.inject(this);
		mVersionTextView.setText(AppUtils.getAppName(this) + "v" + AppUtils.getVersionName(this));
	}

	/**
	 * 点击登录
	 * 
	 * @param v
	 */
	@OnClick(R.id.tv_login)   //登录的点击事件实现
	private void onLoginClick(View v) {
		//获取登录账号
		mAccount = mEtAccountNumber.getText().toString();
		//获取登录密码
		mPassword = mEtPassword.getText().toString();
		//判断账号是否为空，为空提示输入账号
		if (TextUtils.isEmpty(mAccount)) {
			ToastUtils.show(this, "账号不能为空");
			return;
		}
		//判断密码是否为空，为空提示输入密码
		if (TextUtils.isEmpty(mPassword)) {
			ToastUtils.show(this, "密码不能为空");
			return;
		}
		//用户名跟密码都不为空，调用登录函数
		getLogin(mAccount, mPassword);
	}

	@OnClick(R.id.tv_login_teacher)
	private void onTeacherLoginChoose(View v) {
		// mAccount = mEtAccountNumber.getText().toString();
		// mPassword = mEtPassword.getText().toString();
		// if (TextUtils.isEmpty(mAccount)) {
		// ToastUtils.show(this, "账号不能为空");
		// return;
		// }
		// if (TextUtils.isEmpty(mPassword)) {
		// ToastUtils.show(this, "密码不能为空");
		// return;
		// }
		Drawable teacherDrawable = getResources().getDrawable(R.drawable.login_selected);
		teacherDrawable.setBounds(0, 0, teacherDrawable.getMinimumWidth(), teacherDrawable.getMinimumHeight());
		mTeacherChoose.setCompoundDrawables(null, null, teacherDrawable, null);

		Drawable stuDrawable = getResources().getDrawable(R.drawable.login_unselected);
		stuDrawable.setBounds(0, 0, stuDrawable.getMinimumWidth(), stuDrawable.getMinimumHeight());
		mStuChoose.setCompoundDrawables(null, null, stuDrawable, null);

		role = 0;
		// getLogin( mAccount, mPassword);
	}

	@OnClick(R.id.tv_login_stu)
	private void onStuLoginChoose(View v) {
		Drawable teacherDrawable = getResources().getDrawable(R.drawable.login_unselected);
		teacherDrawable.setBounds(0, 0, teacherDrawable.getMinimumWidth(), teacherDrawable.getMinimumHeight());
		mTeacherChoose.setCompoundDrawables(null, null, teacherDrawable, null);

		Drawable stuDrawable = getResources().getDrawable(R.drawable.login_selected);
		stuDrawable.setBounds(0, 0, stuDrawable.getMinimumWidth(), stuDrawable.getMinimumHeight());
		mStuChoose.setCompoundDrawables(null, null, stuDrawable, null);
		role = 1;
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
	}

	private void getLogin(String account, String psw) {
		//接口请求发起响应工具类，由xUtils工具提供
		HttpUtils httpUtils = new HttpUtils();
		//post接口请求参数格式封装，按照接口文档要求添加参数
		ParamUtils param = new ParamUtils();
		param.addBizParam("login_id", account);
		param.addBizParam("psw", psw);
        //设置接口访问请求类型为加密类型，默认为不需要加密
		param.setSecret(true);
		//获取用户登录请求响应地址
		String url = UrlUtils.getUrl("userLogin");
		httpUtils.send(HttpMethod.POST, url, param.getRequestParams(), new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				//请求响应成功，对获取到的数据进行解析
				RespEntity<UserInfo> infos = Parser.toRespEntity(responseInfo, UserInfo.class);
				if (infos.getCode() == 0) 
					ToastUtils.showCenter(LoginActivity.this, "登录成功");
				else 
					ToastUtils.showCenter(LoginActivity.this, infos.getMsg());
			}
			@Override
			public void onFailure(HttpException error, String msg) {
				//访问异常，请求失败
				ToastUtils.showCenter(LoginActivity.this, msg);
			}
		});
	}

//	private void showAlterDialog(UserInfo info) {
//		if (info.getIs_modify_pwd() == 1) {
//			CustomDialog dialog = new CustomDialog.Builder(this).setTitle("修改密码").setMessage("您当前密码为系统初始值,为了账户安全,请去设置里面修改密码.")
//					.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int position) {
//							dialog.dismiss();
//							startIntent();
//						}
//					}).create();
//			dialog.show();
//		} else {
//
//			startIntent();
//		}
//
//	}

	

	/**
	 * 登录成功后跳转到主页
	 * 
	 * @param role
	 *            =0 老师 role=1 学生
	 * 
	 */
//	private void startIntent() {
//		SPUtils.put(this, Const.USER_ROLE, role);
//		Intent intent = new Intent();
//		intent.setClass(this, HomeActivity.class);
//		intent.putExtra(Const.USER_ROLE, role);
//		startActivity(intent);
//		finish();
//	}

	@OnClick(R.id.find_password)
	private void onFindPasswordClick(View v) {
		ToastUtils.showBottom(LoginActivity.this, "该功能正在建设中，敬请期待...");
	}
}