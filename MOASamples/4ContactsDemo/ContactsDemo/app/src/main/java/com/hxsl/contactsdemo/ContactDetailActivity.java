package com.hxsl.contactsdemo;

import com.hxsl.contactsdemo.bean.ContactUserInfo;
import com.hxsl.contactsdemo.http.ParamUtils;
import com.hxsl.contactsdemo.http.Parser;
import com.hxsl.contactsdemo.http.UrlUtils;
import com.hxsl.contactsdemo.util.Const;
import com.hxsl.contactsdemo.util.DialogUtil;
import com.hxsl.contactsdemo.util.ToastUtils;
import com.hxsl.contactsdemo.util.Util;
import com.hxsl.contactsdemo.widget.CircleImage;
import com.lidroid.xutils.BitmapUtils;
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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 联系人详情
 * 
 * @author Administrator
 * 
 */

@ContentView(R.layout.activity_contact_activity)
public class ContactDetailActivity extends Activity {

	@ViewInject(R.id.iv_public_back)
	private ImageView back;  //返回按钮
	@ViewInject(R.id.tv_public_title)
	private TextView title; //标题
	@ViewInject(R.id.iv_public_view)
	private View line;
	@ViewInject(R.id.head_image)
	private CircleImage head_image; //头像
	@ViewInject(R.id.head_name)
	private TextView head_name;  //头像名称
	@ViewInject(R.id.name)
	private TextView name; //昵称名字
	@ViewInject(R.id.position)
	private TextView position; //职务
	@ViewInject(R.id.phone)
	private TextView phone; //手机号码
	@ViewInject(R.id.department)
	private TextView department; //部门
	@ViewInject(R.id.email)
	private TextView email;   //邮箱
	@ViewInject(R.id.dial_number)
	private ImageView teleCall; //座机号
	@ViewInject(R.id.iv_favourite_off)
	private ImageView mOnoff;  //是否已设置为常用联系人

	BitmapUtils bitmapurils;  //xUtils图片加载工具类
	private ContactUserInfo mContactInfo;  //用户数据模型类

	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		dialog = DialogUtil.getprocessDialog(this, "操作执行中....");
		init();
	}

	private void init() {
		//获取人员的信息
		mContactInfo = (ContactUserInfo) getIntent().getSerializableExtra(Const.ACCOUNT);
         //获取图片xUtils图片加载工具类
		bitmapurils = Util.getBitmapUtils(this);
		//传递图片控件，图片请求地址
		bitmapurils.display(head_image, mContactInfo.getAvatar());
		//设置人员名称
		head_name.setText(mContactInfo.getName());
		//设置人员名字
		name.setText(mContactInfo.getName());
		//设置手机号码
		phone.setText(mContactInfo.getMobile_phone());
		//设置部门信息
		department.setText(mContactInfo.getDepartment());
		//设置邮箱号码
		email.setText(mContactInfo.getEmail());
		//设置常用联系人添加/取消开关
		mOnoff.setImageResource(mContactInfo.getIs_collect() == 0 ? R.drawable.button_favourite_off : R.drawable.button_favourite_on);
		if (TextUtils.isEmpty(mContactInfo.getMobile_phone())) {
			teleCall.setVisibility(View.GONE);
		} else {
			teleCall.setVisibility(View.VISIBLE);
		}
		title.setText("联系人信息");
		title.setTextSize(18);
		position.setVisibility(View.GONE);
		line.setVisibility(View.GONE);
	}

	@OnClick(R.id.iv_public_back)
	private void onBackClick(View v) {
		finish();
	}

	@OnClick(R.id.returnto_department)
	private void onReturnClick(View v) {
		// finish();
	}

	@OnClick(R.id.dial_number)
	private void onPhoneClick(View v) {
		//判断手机号码不为空，调用拨号功能
		if (!TextUtils.isEmpty(mContactInfo.getMobile_phone())) {
//			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mContactInfo.getMobile_phone()));
//			startActivity(intent);
		} else {
			ToastUtils.show(getApplicationContext(), "手机号码不能为空");
		}
	}

	private void setFavourite() {
		dialog.show();
		HttpUtils httpUtils = new HttpUtils();
		//请求参数封装
		ParamUtils param = new ParamUtils();
		//获取收藏/取消执行人的用户id
		param.addBizParam("user_id", AppLoader.getInstance().getmUserInfo().getUser_id());
		//添加被收藏人的id
		param.addBizParam("f_user_id", mContactInfo.getId());
		//添加被收藏人的名字
		param.addBizParam("f_user_name", mContactInfo.getName());
		//判断该炒作是收藏/取消动作
		param.addBizParam("isdel", mContactInfo.getIs_collect());
		//获取请求的地址接口
		String url = UrlUtils.getUrl("addfavourite");
		httpUtils.send(HttpMethod.POST, url, param.getRequestParams(), new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				dialog.dismiss();
				//请求开关设置为可点击
				mOnoff.setEnabled(true);
				if (Parser.isSuccess(responseInfo)) {
					if (mContactInfo.getIs_collect() == 0) {
						//收藏成功，开关图片设置为已收藏状态
						ToastUtils.show(ContactDetailActivity.this, "收藏成功");
						mOnoff.setImageResource(R.drawable.button_favourite_on);
						// is_collect=1;
						//修改人员信息收藏的状态
						mContactInfo.setIs_collect(1);
					} else {
						//取消收藏，开关图片设置为未收藏状态
						ToastUtils.show(ContactDetailActivity.this, "取消收藏");
						mOnoff.setImageResource(R.drawable.button_favourite_off);
						// is_collect=0;
						mContactInfo.setIs_collect(0);
					}
				} else {
					ToastUtils.show(ContactDetailActivity.this, Parser.getMsg(responseInfo.result));
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				dialog.dismiss();
				mOnoff.setEnabled(true);
				ToastUtils.show(ContactDetailActivity.this, msg);
			}

		});
	}

	

	@OnClick(R.id.iv_favourite_off)
	private void onOffClick(View v) {
		//点击后，开关设为不可点击状态，请求响应后再打开，防止二次点击事件发生
		mOnoff.setEnabled(false);
		//调用收藏/取消常用联系人函数
		setFavourite();
	}

}
