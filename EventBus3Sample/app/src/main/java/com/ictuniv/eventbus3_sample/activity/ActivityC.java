package com.ictuniv.eventbus3_sample.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ictuniv.eventbus3_sample.R;
import com.ictuniv.eventbus3_sample.event.CloseActivityB;
import com.ictuniv.eventbus3_sample.event.CloseAllActivity;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityC extends AppCompatActivity {

    @BindView(R.id.btn_close_b)
    Button btnCloseB;
    @BindView(R.id.btn_close_ab)
    Button btnCloseAb;
    @BindView(R.id.btn_close_c)
    Button btnCloseC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_close_b, R.id.btn_close_ab, R.id.btn_close_c})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_close_b:
                EventBus.getDefault().post(new CloseActivityB("关闭界面B"));
                break;
            case R.id.btn_close_ab:
                EventBus.getDefault().post(new CloseAllActivity("关闭所有的界面"));
                break;
            case R.id.btn_close_c:
                finish();
                break;
        }
    }
}
