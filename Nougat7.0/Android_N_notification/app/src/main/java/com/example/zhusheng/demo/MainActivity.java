package com.example.zhusheng.demo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private NotificationManager mNotificationManager;

    private int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        ((Button) findViewById(R.id.btn1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //使用NotificationCompat
                Notification notification = new NotificationCompat.Builder(MainActivity.this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("title")
                        .setContentText("You have received an email from 12306")
                        .build();
                mNotificationManager.notify(i++,notification);
            }
        });

        ((Button) findViewById(R.id.btn2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //要处理的意图
                Intent intent = new Intent(MainActivity.this,SecondActivity.class);

                //对输入的内容进行加密处理，参数为密钥，要求最低API 20
                RemoteInput remoteInput =  new RemoteInput.Builder("key 112").build();
                //PendingIntent可以指定用户为点击发送后处理那个intent
                Notification.Action action = new Notification.Action.Builder(R.mipmap.ic_launcher,"请输入回复的内容", PendingIntent.getActivity(MainActivity.this,12,intent,PendingIntent.FLAG_ONE_SHOT))
                        .addRemoteInput(remoteInput)
                        .build();

                //使用Notification，可以回复的Notification
                Notification notification = new Notification.Builder(MainActivity.this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("title")
                        .setContentText("You have received an email from 12306")
                        .addAction(action)
                        .build();
                mNotificationManager.notify(i,notification);
            }
        });
    }
}
