package com.example.android_email;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ReceiveAndSend extends AppCompatActivity {
    private Button btnReceiveEmail;
    private Button btnSendEmail;

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receive_send);
        btnReceiveEmail = (Button) findViewById(R.id.btnReceiveEmail);
        btnSendEmail = (Button) findViewById(R.id.btnSendEmail);

        btnReceiveEmail.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent_second = new Intent();
                intent_second.setClass(ReceiveAndSend.this, ReceiveList.class);
                startActivity(intent_second);
            }
        });
        btnSendEmail.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent_third = new Intent();
                intent_third.setClass(ReceiveAndSend.this, SendMail.class);
                startActivity(intent_third);
            }
        });

    }

//	protected Dialog onCreateDialog(int id) {// 显示网络连接Dialog
//		ProgressDialog dialog = new ProgressDialog(this);
//		dialog.setTitle("请稍候。。。");
//		dialog.setIndeterminate(true);
//		dialog.setMessage("程序正在加载。。。");
//		return dialog;
//	}

}
