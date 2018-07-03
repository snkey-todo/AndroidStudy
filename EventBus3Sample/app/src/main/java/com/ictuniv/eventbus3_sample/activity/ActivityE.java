package com.ictuniv.eventbus3_sample.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.ictuniv.eventbus3_sample.R;
import com.ictuniv.eventbus3_sample.event.LoginInfoBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityE extends AppCompatActivity {

    @BindView(R.id.tv_sticky_event)
    TextView tvStickyEvent;
    @BindView(R.id.btn_finish)
    Button btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e);
        ButterKnife.bind(this);


        /*
            注意：当注册的时候，粘性事件会立刻被post给订阅者，所以注意注册的位置，
            如果在findviewbyid之前注册，那么注册时候就会执行下面订阅中的方法，导致空指针异常！
         */
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.btn_finish)
    public void onClick() {
        finish();
    }

    /**
     * 订阅粘性事件LoginInfoBean
     *
     * @param event
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getLoginInfo(LoginInfoBean event) {
        tvStickyEvent.setText("登陆名：代高凯\n登陆时间：2016-6-6\n登陆地点：北京\n经度：100\n纬度：100\n" + event.msg);
    }
}
