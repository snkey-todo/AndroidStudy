package com.ictuniv.eventbus3_sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ictuniv.eventbus3_sample.event.LoginEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.getData)
    Button getData;
    @BindView(R.id.activity_login)
    RelativeLayout activityLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.getData)
    public void onClick() {
        EventBus.getDefault().post(new LoginEvent(true,"zhangsan","123456"));
        Toast.makeText(this, "登录成功！", Toast.LENGTH_SHORT).show();
        finish();
    }
}
