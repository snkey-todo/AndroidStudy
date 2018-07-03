package com.ictuniv.eventbus3_sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ictuniv.eventbus3_sample.R;
import com.ictuniv.eventbus3_sample.event.CloseAllActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityA extends AppCompatActivity {
    private static final String TAG = "ActivityA";
    @BindView(R.id.btn_open_b)
    Button btnOpenB;
    @BindView(R.id.btn_close)
    Button btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
    @OnClick({R.id.btn_open_b, R.id.btn_close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_open_b:
                startActivity(new Intent(ActivityA.this, ActivityB.class));  // 打开界面B
                break;
            case R.id.btn_close:
                finish();
                break;
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closeActivity(CloseAllActivity event) {
        Log.e(TAG, event.getMsg());
        finish();
    }
}
