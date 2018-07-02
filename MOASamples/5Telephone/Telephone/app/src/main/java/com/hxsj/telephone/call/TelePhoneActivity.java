package com.hxsj.telephone.call;

import java.util.HashMap;

import org.webrtc.EglBase;

import com.hxsj.telephone.R;
import com.hxsj.telephone.bean.MsgType;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.hxsj.telephone.observer.MessageObserver;
import com.hxsj.telephone.observer.Observer;
import com.hxsj.telephone.observer.ObserverFilter;
import com.hxsj.telephone.util.Const;
import com.hxsj.telephone.widget.TimerTextView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@ContentView(R.layout.activity_telephone)
public class TelePhoneActivity extends Activity {
	public static final boolean EXTRA_TRACING = false;
	@ViewInject(R.id.tv_connecting)
	private TextView tvConnecting;  //连接状态
	@ViewInject(R.id.tv_connect_time)
	private TimerTextView tvConnecttime; //通话时长
	@ViewInject(R.id.tv_telname)
	private TextView mtelname;   //呼叫者名称
	@ViewInject(R.id.button_call_toggle_mic)
	private LinearLayout mMicCall;   //静音开启控件
	@ViewInject(R.id.button_call_holder)
	private LinearLayout mCallHolder;   //免提模式开启
	
	@ViewInject(R.id.button_answer)
	private LinearLayout mAnswerView;  //接听电话
	@ViewInject (R.id.button_huang_up)
	private LinearLayout mHuangUpView; //挂断电话
	@ViewInject(R.id.iv_callholder)
	private ImageView ivCallholder; //开启免提图片
	@ViewInject(R.id.iv_toggle_mic)
	private ImageView ivToggleMic; //静音模式图片

	private String userid; //用户id
	private String username; //用户名称
	// 呼叫类型，true，为主动呼叫，false为被动呼叫请求
	private boolean mtype;
	private int currVolume; //音量
	private boolean isCallHolder = false; //是否开启免提
	private boolean micEnabled = true;  //是否开启静音
	private AppRTCAudioManager audioManager = null;  //通话模式工具类

	//提示音工具播放类
	private SoundPool soundPool;
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, Integer> soundPoolMap = new HashMap<Integer, Integer>();

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		ViewUtils.inject(this);
		//初始化页面布局
		initData();

		//创建通话连接信道
		PeerConnectionClient.getInstance().createPeerConnection(EglBase.create().getEglBaseContext(), null, null);
		//注册通话取消观察者observer,监听通话取消时间
		MessageObserver.getInstance().registerObserver(observer, new ObserverFilter(Const.APPRTC_CANCEL));
	}

	Observer observer = new Observer() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void notifyChanged(Object object) {
			String command = (String) object;
			if (soundPool != null) {
				//释放掉提示音播放
				soundPool.autoPause();
				soundPool.release();
				AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				audioManager.setSpeakerphoneOn(false);
			}
			if (MsgType.CANCEL.equals(command)) {
				// 通话取消
				tvConnecting.setText("通话连接中断");
				tvConnecttime.stopRun();
				handler.sendEmptyMessageDelayed(3, 500);
			} else if (MsgType.CONNECTING.equals(command)) {
				// 通话建立成功
				handler.sendEmptyMessage(4);
			}
		}
	};

	@SuppressWarnings("deprecation")
	private void initData() {
		//接收上级页面传递过来的数据
		userid = getIntent().getStringExtra(Const.PERSON_ID);
		username = getIntent().getStringExtra(Const.PERSON_NAME);
		mtype = getIntent().getBooleanExtra(Const.TYPE, false);
		mtelname.setText(username);
		//加载提所有的示音
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		soundPoolMap.put(1, soundPool.load(this, R.raw.phone_call, 1));
		soundPoolMap.put(2, soundPool.load(this, R.raw.phone_listener, 2));
		// 默认免提功能关闭
		isCallHolder = false;
		if (mtype) {
			// 是自己，则为主动呼叫，设置对应的页面布局
			tvConnecting.setVisibility(View.VISIBLE);
			tvConnecting.setText("正在连接对方...");
			mHuangUpView.setVisibility(View.VISIBLE);
			mCallHolder.setVisibility(View.GONE);
			mMicCall.setVisibility(View.GONE);
			mAnswerView.setVisibility(View.GONE);
			//开始调用开启语音
			startCall();
			handler.sendEmptyMessageDelayed(1, 1000);
		} else {
			// 不是自己,被动呼叫，设置对应的页面布局
			mHuangUpView.setVisibility(View.VISIBLE);
			mCallHolder.setVisibility(View.GONE);
			mMicCall.setVisibility(View.GONE);
			mAnswerView.setVisibility(View.VISIBLE);
			tvConnecting.setVisibility(View.VISIBLE);
			tvConnecting.setText("邀请您进行语音通话");
			handler.sendEmptyMessageDelayed(2, 2000);
		}
		tvConnecttime.setVisibility(View.INVISIBLE);
	}

	public void playSound(int sound, int loop) {
		AudioManager mgr = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		mgr.setSpeakerphoneOn(true);
		float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = streamVolumeMax;
		soundPool.play(soundPoolMap.get(sound), volume, volume, 1, loop, 1f);
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				playSound(1, -1);
				MessageObserver.getInstance().notifyDataChanged(new MsgType(MsgType.SEND_CALL, userid), new ObserverFilter(Const.APPRTC_STATE));
				break;
			case 2:
				playSound(2, -1);
				break;
			case 3:
				finish();
				break;
			case 4:
				tvConnecting.setVisibility(View.VISIBLE);
				tvConnecting.setText("通话中...");
				tvConnecttime.setVisibility(View.VISIBLE);
				mMicCall.setVisibility(View.VISIBLE);
				mCallHolder.setVisibility(View.VISIBLE);
				mAnswerView.setVisibility(View.GONE);
				mHuangUpView.setVisibility(View.VISIBLE);
				tvConnecttime.beginRun();
				break;
			}
		}
	};

	private void startCall() {
		//工具类初始化
		audioManager = AppRTCAudioManager.create(this, null);
		audioManager.init();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		//注销掉当前页面的监听事件
		MessageObserver.getInstance().unregisterObserver(observer);
		if (soundPool != null) {
			//如果soundPool没被释放，释放掉
			soundPool.autoPause();
			soundPool.release();
		}
		if (audioManager != null) {
			//释放语音通话
			audioManager.close();
			audioManager = null;
		}
		super.onDestroy();
	}

	// 打开扬声器
	@SuppressWarnings("deprecation")
	public void OpenSpeaker() {

		try {
			AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			audioManager.setMode(AudioManager.ROUTE_SPEAKER);
			currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

			if (!audioManager.isSpeakerphoneOn()) {
				audioManager.setSpeakerphoneOn(true);

				audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.STREAM_VOICE_CALL);
			}
			isCallHolder = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 关闭扬声器
	public void CloseSpeaker() {

		try {
			isCallHolder = false;
			AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			if (audioManager != null) {
				if (audioManager.isSpeakerphoneOn()) {
					audioManager.setSpeakerphoneOn(false);
					audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume, AudioManager.STREAM_VOICE_CALL);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@OnClick(R.id.button_call_holder)
	private void onCallHolderClick(View v) {
		//免提模式开启关闭实现
		if (isCallHolder) {
			//关闭扬声器实现
			CloseSpeaker();
		} else {
			//开启扬声器实现
			OpenSpeaker();
		}
	    //改变免提模式的图片显示
		ivCallholder.setBackgroundResource(isCallHolder ? R.drawable.quiet_click : R.drawable.quiet);
	}

	@OnClick(R.id.button_call_toggle_mic)
	private void onCallMicClick(View v) {
		// 开启禁音模式
		micEnabled = !micEnabled;
		//改变静音模式开关的图片
		ivToggleMic.setBackgroundResource(micEnabled ? R.drawable.speakerphone_click :R.drawable.speakerphone);
		//通知上一个模块中的telephoneObserver通话模式改变
		MessageObserver.getInstance().notifyDataChanged(new MsgType(MsgType.AUDIO_ENABLED, micEnabled), new ObserverFilter(Const.APPRTC_STATE));
	}

	@OnClick(R.id.button_huang_up)
	private void onHuangUpClick(View v) {
		//释放掉提示音
		soundPool.autoPause();
		soundPool.release();
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audioManager.setSpeakerphoneOn(false);
		//停止计时
		if (tvConnecttime.isShown()) {
			tvConnecttime.stopRun();
		}
		//通知上一个模块中的telephoneObserver通话一被挂断
		MessageObserver.getInstance().notifyDataChanged(new MsgType(MsgType.BE_RESUE, userid), new ObserverFilter(Const.APPRTC_STATE));
		finish();
	}

	@OnClick(R.id.button_answer)
	private void onAnswerClick(View v) {
		// 接听通话，停止和释放掉提示音
		soundPool.autoPause();
		soundPool.release();

		//默认设置扬声器关闭
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audioManager.setSpeakerphoneOn(false);
        mHuangUpView.setVisibility(View.VISIBLE);
        mAnswerView.setVisibility(View.GONE);
        mCallHolder.setVisibility(View.VISIBLE);
        mMicCall.setVisibility(View.VISIBLE);
		tvConnecting.setVisibility(View.VISIBLE);
		tvConnecting.setText("通话中...");
		//通知上一个模块中的telephoneObserver通话已被接通
		MessageObserver.getInstance().notifyDataChanged(new MsgType(MsgType.ANSWER, userid), new ObserverFilter(Const.APPRTC_STATE));
	}


}
