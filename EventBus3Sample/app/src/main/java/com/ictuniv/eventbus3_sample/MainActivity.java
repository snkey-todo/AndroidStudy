package com.ictuniv.eventbus3_sample;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ictuniv.eventbus3_sample.activity.ActivityA;
import com.ictuniv.eventbus3_sample.activity.ActivityD;
import com.ictuniv.eventbus3_sample.event.LoginInfoBean;
import com.ictuniv.eventbus3_sample.event.MessageEventA;
import com.ictuniv.eventbus3_sample.event.MessageEventB;
import com.ictuniv.eventbus3_sample.event.MessageEventC;
import com.ictuniv.eventbus3_sample.event.MessageEventD;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ictuniv.eventbus3_sample.R.id.tv_content;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_post_from_main)
    Button btnPostFromMain;
    @BindView(R.id.btn_post_from_sub)
    Button btnPostFromSub;
    @BindView(R.id.btn_main_from_main)
    Button btnMainFromMain;
    @BindView(R.id.btn_main_from_sub)
    Button btnMainFromSub;
    @BindView(R.id.btn_bg_from_main)
    Button btnBgFromMain;
    @BindView(R.id.btn_bg_from_sub)
    Button btnBgFromSub;
    @BindView(R.id.btn_async_from_main)
    Button btnAsyncFromMain;
    @BindView(R.id.btn_async_from_sub)
    Button btnAsyncFromSub;
    @BindView(tv_content)
    TextView tvContent;
    @BindView(R.id.btn_open_a)
    Button btnOpenA;
    @BindView(R.id.btn_post_data)
    Button btnPostData;
    @BindView(R.id.btn_send_sticky_event)
    Button btnSendStickyEvent;

    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.btn_post_from_main, R.id.btn_post_from_sub, R.id.btn_main_from_main, R.id.btn_main_from_sub, R.id
            .btn_bg_from_main, R.id.btn_bg_from_sub, R.id.btn_async_from_main, R.id.btn_async_from_sub, R.id
            .btn_open_a, R.id.btn_post_data, R.id.btn_send_sticky_event})
    public void onClick(View view) {
        switch (view.getId()) {

            ////////////ThreadMode  POSTING//////////////////////
            case R.id.btn_post_from_main: //主线程发送event - 主线程接收
                EventBus.getDefault().post(new MessageEventA("Hi, post girl! I'm from UI Thread.", "20160105"));
                break;
            case R.id.btn_post_from_sub: //子线程发送event - 子线程接收
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        EventBus.getDefault().post(new MessageEventA("Hi, post girl! I'm from Sub Thread.",
                                "20160105"));
                    }
                }.start();
                break;


            ////////////ThreadMode  MAIN//////////////////////
            case R.id.btn_main_from_main://主线程发送event - 主线程立刻执行
                EventBus.getDefault().post(new MessageEventB("Hi, main boy! I'm from UI Thread."));
                break;
            case R.id.btn_main_from_sub://子线程发送event - 主线程接收
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        EventBus.getDefault().post(new MessageEventB("Hi, main boy! I'm from Sub Thread."));
                    }
                }.start();
                break;


            ////////////ThreadMode  BACKGROUND//////////////////////
            case R.id.btn_bg_from_main://主线程发送event - 开子线程执行
                EventBus.getDefault().post(new MessageEventC("Hi, background boy! I'm from UI Thread."));
                break;
            case R.id.btn_bg_from_sub://子线程发送event - 子线程立刻执行
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        EventBus.getDefault().post(new MessageEventC("Hi, background boy! I'm from Sub Thread."));
                    }
                }.start();
                break;
            ////////////ThreadMode  ASYNC//////////////////////


            case R.id.btn_async_from_main://主线程发送event - 开子线程执行
                EventBus.getDefault().post(new MessageEventD("Hi, background boy! I'm from UI Thread."));
                break;
            case R.id.btn_async_from_sub://子线程发送event - 开子线程执行
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        EventBus.getDefault().post(new MessageEventD("Hi, background boy! I'm from Sub Thread."));
                    }
                }.start();
                break;


            //////////////////////////////////
            case R.id.btn_open_a://用法一：指定关闭打开的界面
                startActivity(new Intent(MainActivity.this, ActivityA.class));  // 打开界面A
                break;
            case R.id.btn_post_data://用法二：在Activity、Fragment中传递数据
                startActivity(new Intent(this, SecondActivity.class));
                break;
            case R.id.btn_send_sticky_event://用法三：发布粘性事件(Sticky Event),并打开新的界面
                index++;
                EventBus.getDefault().postSticky(new LoginInfoBean("" + index));
                Toast.makeText(MainActivity.this, "发布了粘性事件-登陆信息", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, ActivityD.class));  // 打开界面D
                break;
        }
    }
    ////////四种订阅模式//////

    /**
     * POSTING模式，事件从哪个线程发布，事件处理函数就在哪个线程中执行
     * 函数名称可以随意取
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMessageEventA(MessageEventA event) {
        final String content = event.getMsg()
                + event.getTime()
                + System.getProperty("line.seprator", "\n")
                + "from Thread-Id:" + Thread.currentThread()
                + ", Name:" + Thread.currentThread().getName();
        //确保在UI线程修改UI界面
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvContent.setText(content);
            }
        });
    }

    /**
     * MAIN模式，无论从哪个线程发布事件，事件处理函数都会在UI线程中执行
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventB(MessageEventB event) {
        final String content = event.getMsg() + System.getProperty("line.separator", "\n")
                + "from  Thread-Id：" + Thread.currentThread().getId()
                + " , Name：" + Thread.currentThread().getName();

        // 确保修改UI界面是在UI线程进行
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvContent.setText(content);
            }
        });
    }

    /**
     * BACKGROUND模式，如果事件由UI线程发出，事件处理函数在新的子线程中执行
     * 如果事件由子线程发出，事件处理函数在当前线程中执行
     * 内部维护线程池！！！
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEventC(MessageEventC event) {
        final String content = event.getMsg() + System.getProperty("line.separator", "\n")
                + "from  Thread-Id：" + Thread.currentThread().getId()
                + " , Name：" + Thread.currentThread().getName();

        // 确保修改UI界面是在UI线程进行
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvContent.setText(content);
            }
        });
    }

    /**
     * ASYNC模式，无论在那个线程中发出事件，事件处理函数都会在新建的子线程中执行
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMessageEventD(MessageEventD event) {
        final String content = event.getMsg() + System.getProperty("line.separator", "\n")
                + "from  Thread-Id：" + Thread.currentThread().getId()
                + " , Name：" + Thread.currentThread().getName() + System.getProperty("line.separator", "\n")
                + Thread.getAllStackTraces().size();

        // 确保修改UI界面是在UI线程进行
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvContent.setText(content);
                Toast.makeText(MainActivity.this, "子线程沉睡3s!", Toast.LENGTH_SHORT).show();
            }
        });

        SystemClock.sleep(3000);
    }
}
