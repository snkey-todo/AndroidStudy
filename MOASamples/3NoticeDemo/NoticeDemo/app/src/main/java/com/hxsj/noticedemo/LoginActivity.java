package com.hxsj.noticedemo;




import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hxsj.noticedemo.bean.UserInfo;
import com.hxsj.noticedemo.http.ParamUtils;
import com.hxsj.noticedemo.http.Parser;
import com.hxsj.noticedemo.http.RespEntity;
import com.hxsj.noticedemo.http.UrlUtils;
import com.hxsj.noticedemo.util.AppUtils;
import com.hxsj.noticedemo.util.SPUtils;
import com.hxsj.noticedemo.util.ToastUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.http.callback.RequestCallBack;
@ContentView(R.layout.activity_login)
public class LoginActivity extends Activity {


	@ViewInject(R.id.et_login_account_number)
	private EditText mEtAccountNumber;
	@ViewInject(R.id.et_login_password)
	private EditText mEtPassword;
	@ViewInject(R.id.tv_login)
	private TextView mLogin;
	@ViewInject(R.id.tv_login_version)
	private TextView mVersionTextView;
	@ViewInject(R.id.tv_login_teacher)
	private TextView mTeacherChoose;
	@ViewInject(R.id.tv_login_stu)
	private TextView mStuChoose;
//	private Dialog mLoginDialog;

	private String mAccount;
	private String mPassword;

	private int role;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		ViewUtils.inject(this);

		mVersionTextView.setText(AppUtils.getAppName(this) + "v" + AppUtils.getVersionName(this));
//		mLoginDialog = DialogUtil.getLoginDialog(this);

	}

	/**
	 * 点击登录
	 * 
	 * @param v
	 */
	@OnClick(R.id.tv_login)
	private void onLoginClick(View v) {

		mAccount = mEtAccountNumber.getText().toString();
		mPassword = mEtPassword.getText().toString();
		if (TextUtils.isEmpty(mAccount)) {
			ToastUtils.show(this, "账号不能为空");
			return;
		}
		if (TextUtils.isEmpty(mPassword)) {
			ToastUtils.show(this, "密码不能为空");
			return;
		}

//		if (mLoginDialog != null && !mLoginDialog.isShowing())
//			mLoginDialog.show();
		// role = 0;
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

		HttpUtils httpUtils = new HttpUtils();

		ParamUtils param = new ParamUtils();
		param.addBizParam("login_id", account);
		param.addBizParam("psw", psw);

		param.setSecret(true);
		String url = UrlUtils.getUrl("userLogin");

		httpUtils.send(HttpMethod.POST, url, param.getRequestParams(), new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				

				RespEntity<UserInfo> infos = Parser.toRespEntity(responseInfo, UserInfo.class);
				if (infos.getCode() == 0) {
					ToastUtils.showCenter(LoginActivity.this, "登录成功");
					UserInfo userInfo = infos.getData();
					userInfo.setLogin_pwd("12345");
					saveData(userInfo);
					
					AppLoader.getInstance().setmUserInfo(userInfo);
					Intent intent=new Intent();
					intent.setClass(LoginActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
//					showAlterDialog(userInfo);
				} else {
					ToastUtils.showCenter(LoginActivity.this, infos.getMsg());
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {

				ToastUtils.showCenter(LoginActivity.this, msg);
			}

		});
	}
	public  void saveData(UserInfo info) {

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