package com.example.android_email;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText txtEmailAddress;
    private EditText txtPWD;
    private Button btnOK, btn_test;
    private Spinner emailTypeSpinner;
    private static final String SAVE_INFORMATION = "save_information";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtEmailAddress = (EditText) findViewById(R.id.txtEmailAddress);
        txtPWD = (EditText) findViewById(R.id.txtPWD);
        btnOK = (Button) findViewById(R.id.btnOK);
        btn_test = (Button) findViewById(R.id.btn_test);
        emailTypeSpinner = (Spinner) findViewById(R.id.emailType);
        List<EmailType> lst = new ArrayList<EmailType>();
        lst.add(new EmailType(1, "网易163"));
        lst.add(new EmailType(2, "网易126"));
        lst.add(new EmailType(3, "腾讯"));
        lst.add(new EmailType(4, "搜狐"));
        lst.add(new EmailType(5, "新浪CN"));
        lst.add(new EmailType(6, "新浪COM"));
        lst.add(new EmailType(7, "雅虎"));
        ArrayAdapter<EmailType> adapter = new ArrayAdapter<EmailType>(this, android.R.layout.simple_spinner_item, lst);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emailTypeSpinner.setAdapter(adapter);
        emailTypeSpinner.requestFocus();
        // 给EditText进行 初始化付值，以方便运行程序


        btnOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // 获得编辑器
                SharedPreferences.Editor editor = getSharedPreferences(SAVE_INFORMATION, MODE_WORLD_WRITEABLE).edit();
                // 将EditText文本内容添加到编辑器
                editor.putString("save", txtEmailAddress.getText().toString() + ";" + txtPWD.getText().toString());
                EmailType emailType = (EmailType) emailTypeSpinner.getSelectedItem();
                switch (emailType.getID()) {
                    case 1:// 网易163
                        editor.putString("receive_host", "pop.163.com");
                        editor.putString("send_host", "smtp.163.com");
                        break;
                    case 2:// 网易126
                        editor.putString("receive_host", "pop.126.com");
                        editor.putString("send_host", "smtp.126.com");
                        break;
                    case 3:// 腾讯
                        editor.putString("receive_host", "pop.qq.com");
                        editor.putString("send_host", "smtp.qq.com");
                        break;
                    case 4:// 搜狐
                        editor.putString("receive_host", "pop.sohu.com");
                        editor.putString("send_host", "smtp.sohu.com");
                        break;
                    case 5:// 新浪cn
                        editor.putString("receive_host", "pop.sina.cn");
                        editor.putString("send_host", "smtp.sina.cn");
                        break;
                    case 6:// 新浪com
                        editor.putString("receive_host", "pop.sina.com");
                        editor.putString("send_host", "smtp.sina.com");
                        break;
                    case 7:// 雅虎
                        editor.putString("receive_host", "pop.mail.yahoo.com.cn");
                        editor.putString("send_host", "smtp.mail.yahoo.com.cn");
                        break;
                }
                editor.commit(); // 提交编辑器内容
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ReceiveAndSend.class);
                startActivity(intent);
            }
        });

        btn_test.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("登陆错误" + "\n" + "可能是一下原因造成的");
        builder.setMessage("请尝试以下操作:\n" + "1.在QQ网页邮箱开启IMAP服务，并使用授权码登陆\n" + "2.在QQ安全中心关闭邮箱登陆保护");
        builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }


        });
        builder.setPositiveButton("如何开启IMAP", new android.content.DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent();
                intent.setClass(MainActivity.this, QQMailSuggests.class);
                startActivity(intent);
            }

        });
        builder.create();
        builder.show();
    }
}
