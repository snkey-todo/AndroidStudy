package com.example.zhusheng.android_n_java8;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
        //支持代码块,()相当于run方法的(),如果有参数请填写参数
        new Thread(
                () -> {
                    Log.i(TAG, "java 8 lambda表达式");
                }
        ).start();
        //支持语句
        new Thread(
                () -> Log.i(TAG, "java 8 lambda表达式")
        ).start();

        ((Button) findViewById(R.id.btn1)).setOnClickListener(
                (view)->{
                    Log.e(TAG,"you have clicked the button");
                }
        );
    }
}
