package com.ictuniv.eventbus3_sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ictuniv.eventbus3_sample.R;
import com.ictuniv.eventbus3_sample.event.CloseActivityB;
import com.ictuniv.eventbus3_sample.event.CloseAllActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityB extends AppCompatActivity {
    private static final String TAG = "ActivityB";
    @BindView(R.id.btn_open_c)
    Button btnOpenC;
    @BindView(R.id.btn_close_b)
    Button btnCloseB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @OnClick({R.id.btn_open_c, R.id.btn_close_b})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_open_c:
                startActivity(new Intent(ActivityB.this, ActivityC.class));  // 打开界面C
                break;
            case R.id.btn_close_b:
                finish();
                break;
        }
    }
    //接收CloseActivityB事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closeActivity(CloseActivityB event) {
        Log.e(TAG, event.getMsg());
        finish();
    }
    //接收CloseAllActivity事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closeActivity(CloseAllActivity event) {
        Log.e(TAG, event.getMsg());
        finish();
    }
}
