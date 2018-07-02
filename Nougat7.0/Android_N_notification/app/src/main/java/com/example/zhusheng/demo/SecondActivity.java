package com.example.zhusheng.demo;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {
    private NotificationManager mNotificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        TextView tv = (TextView) findViewById(R.id.tv);

        //显示接收的通知
        Intent intent = getIntent();
        Bundle bundle = RemoteInput.getResultsFromIntent(intent);
        CharSequence charSequence = bundle.getCharSequence("key 112");
        tv.setText(charSequence);


        //对通知进行处理
        Notification notification = new Notification.Builder(SecondActivity.this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("title")
                .setContentText("You has reply")
                .build();
        mNotificationManager.notify(1,notification);
    }
}
