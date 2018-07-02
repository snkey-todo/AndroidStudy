package com.hxsj.telephone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hxsj.telephone.bean.MsgType;
import com.hxsj.telephone.call.AppRTCClient;
import com.hxsj.telephone.call.PeerConnectionClient;
import com.hxsj.telephone.call.TelePhoneActivity;
import com.hxsj.telephone.call.WebSocketRTCClient;
import com.hxsj.telephone.http.UrlUtils;
import com.hxsj.telephone.observer.MessageObserver;
import com.hxsj.telephone.observer.Observer;
import com.hxsj.telephone.observer.ObserverFilter;
import com.hxsj.telephone.util.AppRTCUtils;
import com.hxsj.telephone.util.Const;
import com.hxsj.telephone.util.LooperExecutor;
import com.hxsj.telephone.util.ToastUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 继承webRTC操作指令接口，在这里处理，进入/退出等操作
 * **/
@ContentView(R.layout.activity_task)
public class MiddleActivity extends Activity implements AppRTCClient.SignalingEvents {

	@ViewInject(R.id.tv_public_title)
	private TextView title;  //标题

	@ViewInject(R.id.et_account)
	private EditText myAccount;  //设定自己账号输入框
	@ViewInject(R.id.et_other_account)
	private EditText otherAccount;  //被呼叫账号输入框


	private AppRTCClient appRtcClient;  //webrtc工具操作类
	private String remoteUserId = "";   //被呼叫的账号id
	private boolean isInit=false; //是否执行了连接到服务端的操作
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		title.setText("语音电话");
		findViewById(R.id.iv_public_back).setVisibility(View.GONE);

	}
	@OnClick(R.id.button1)
	private void onButtonClick(View view){
		//为自己设定一个唯一的账号，连接到语音服务器
		String number=myAccount.getText().toString();
		if (TextUtils.isEmpty(number)){
			ToastUtils.show(this,"请先为自己设定一个账号");
			return;
		}
      //初始化操作，连接到通话服务器
		initTelePhone(number);
	}
	@OnClick(R.id.button2)
	private void onCallClick(View v){
		//拨打语音电话
		String account=otherAccount.getText().toString();
		if (TextUtils.isEmpty(account)){
			ToastUtils.show(this,"请输入有效的拨打账号");
			return;
		}
		if (!isInit){
			ToastUtils.show(this,"请先创建连接");
			return;
		}
		//发送通知，告知准备拨号啦
		MessageObserver.getInstance().notifyDataChanged(new MsgType(MsgType.CALL, account, account), new ObserverFilter(Const.APPRTC_STATE));
	}
	private void initTelePhone(String uid) {
        //通过UrlUtils.getTelePhoneUrl()获取通话服务器地址
		String roomUrl = UrlUtils.getTelePhoneUrl();
		//初始化语音通话工具类
		appRtcClient = new WebSocketRTCClient(this, new LooperExecutor(), this);
		//尝试连接到语音服务器，这里需要传递服务器地址，"120"是服务端设置的语音通话的房间号，当前自己注册的账号
		appRtcClient.connectToRoom(new AppRTCClient.RoomConnectionParameters(roomUrl, "120", uid));
       // 标识已经连接过服务器
		isInit=true;
		//注册通话事件 观察者Observer,在observer中负责响应事件处理
		MessageObserver.getInstance().registerObserver(telePhoneObserver, new ObserverFilter(Const.APPRTC_STATE));
	}

	Observer telePhoneObserver = new Observer() {

		@Override
		public void notifyChanged(Object object) {
			MsgType state = (MsgType) object;
			if (MsgType.ANSWER.equals(state.getType())) {
				// 执行接听操作
				PeerConnectionClient.getInstance().createOffer();
			} else if (MsgType.BE_RESUE.equals(state.getType())) {
				// 被叫方拒绝接听
				cancel(state.getUserid());
			} else if (MsgType.RESUE.equals(state.getType())) {
				// 自己主动挂断
				cancel(state.getUserid());
			} else if (MsgType.SEND_CALL.equals(state.getType())) {

				JSONObject json = new JSONObject();

				AppRTCUtils.jsonPut(json, "type", "call");
				AppRTCUtils.jsonPut(json, "username", state.getUsername());
				appRtcClient.send(state.getUserid(), json);
				// MessageObserver.getInstance().notifyDataChanged(new
				// MsgType(MsgType.CONNECTING, state.getUserid()), new
				// ObserverFilter(Const.APPRTC_CANCEL));
			} else if (MsgType.CALL.equals(state.getType())) {
				call(state.getUserid(), state.getUsername());
			} else if (MsgType.AUDIO_ENABLED.equals(state.getType())) {
				// 设置静音
				PeerConnectionClient.getInstance().setAudioEnabled(state.isEnabled());
			}

		}
	};
	private void call(String userId, String username) {
		//跳转到通话页面，
		Intent intent = new Intent();
		intent.setClass(this, TelePhoneActivity.class);
		intent.putExtra(Const.TYPE, true);
		intent.putExtra(Const.PERSON_ID, userId);
		intent.putExtra(Const.PERSON_NAME, username);
		remoteUserId = userId;
		// 创建与对方会话的连接
		appRtcClient.SetRemoteUserId(userId);
		appRtcClient.createPeerConnectionFactory();
		startActivity(intent);
	}

	private void cancel(String userId) {
		//关闭信令交互
		PeerConnectionClient.getInstance().close();
		if (TextUtils.isEmpty(userId) || !userId.equals(remoteUserId))
			return;
		JSONObject json = new JSONObject();
		AppRTCUtils.jsonPut(json, "type", "cancel");
		//取消指令封装，告知语音服务器，已经取消本次通话
		appRtcClient.send(userId, json);
		remoteUserId = "";
	}



	@Override
	public void onLogin(JSONArray userIds) {  //对方登录事件处理，这里暂时不需要用到，所以不处理

	}

	@Override
	public void onLeave(final String userId) {   //对方离开或者连接中断
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (remoteUserId.equals(userId))  //发送通知，告知到通话已经被中断或者取消
					MessageObserver.getInstance().notifyDataChanged(MsgType.CANCEL, new ObserverFilter(Const.APPRTC_CANCEL));
			}
		});
	}

	@Override
	public void onJoin(String userId) {  //用户加入指令处理指令，多人会话中，需要处理该指令

	}

	@Override
	public void onCall(String userId, String username) {
		//接收到呼叫操作
		if (!TextUtils.isEmpty(remoteUserId)) {
			JSONObject json = new JSONObject();
			AppRTCUtils.jsonPut(json, "type", "calling");
			//封装通话指令，发送到服务端，告知已收到
			appRtcClient.send(userId, json);
			return;
		}
		//跳转到通话页面。
		Intent intent = new Intent();
		intent.setClass(this, TelePhoneActivity.class);
		intent.putExtra(Const.TYPE, false);
		intent.putExtra(Const.PERSON_ID, userId);
		intent.putExtra(Const.PERSON_NAME, username);
		remoteUserId = userId;
		// romoteUserName=username;
		//创建通话连接
		appRtcClient.SetRemoteUserId(userId);
		appRtcClient.createPeerConnectionFactory();
		startActivity(intent);

	}

	@Override
	public void onCalling(String userId) {
		//长时间没有响应，通知界面相关处理
		MessageObserver.getInstance().notifyDataChanged(MsgType.CANCEL, new ObserverFilter(Const.APPRTC_CANCEL));
		//关闭信令交流处理
		PeerConnectionClient.getInstance().close();
		remoteUserId = "";
		// romoteUserName="";
		ToastUtils.show(this, "对方正忙...");
	}

	@Override
	public void onCancel(String userId) {
		//通话取消，通知界面相关处理
		MessageObserver.getInstance().notifyDataChanged(MsgType.CANCEL, new ObserverFilter(Const.APPRTC_CANCEL));
		//关闭信令交流处理
		PeerConnectionClient.getInstance().close();
		remoteUserId = "";
	}

	@Override
	public void onChannelClose() {
    //通道关闭
	}

	@Override
	public void onChannelError(String description) {
      //通道错误
	}
}
