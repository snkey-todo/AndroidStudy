package com.example.taskdemo;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.taskdemo.adapter.TasksAdapter;
import com.example.taskdemo.bean.TaskItem;
import com.example.taskdemo.bean.TasksData;
import com.example.taskdemo.http.ParamUtils;
import com.example.taskdemo.http.Parser;
import com.example.taskdemo.http.UrlUtils;
import com.example.taskdemo.util.ToastUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_task)
public class TaskActivity extends Activity {

    @ViewInject(R.id.tasklistView)
    private ListView listview;

    private List<TaskItem> list = new ArrayList<TaskItem>();
    private int page = 1;
    private int page_limit = 20;
    private Dialog dialog;
    private TasksAdapter adapter;
    private String user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        user_id = getIntent().getStringExtra("user_id");
        dialog = new Dialog(TaskActivity.this);
        dialog.setTitle("数据获取中...");
        adapter = new TasksAdapter(TaskActivity.this, list);
        listview.setAdapter(adapter);
    }

    @OnClick(R.id.button1)
    private void onButtonClick(View view) {
        getTaskList();
    }

    @OnClick(R.id.back)
    private void onBackClick(View view) {
        finish();
    }

    private void getTaskList() {
        dialog.show();
        listview.setVisibility(View.VISIBLE);
        HttpUtils httpUtils = new HttpUtils();
        //通过Urlutils类中的geturl()方法获取url地址，同时把传入的参数替换到相应的位置
        String url = UrlUtils.getUrl("getTaskList", user_id, page, page_limit, "", 0, 0);
        httpUtils.send(HttpMethod.GET, url, new ParamUtils().getBaseRequestParams(), new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                dialog.dismiss();
                list.clear();
                if (Parser.isSuccess(responseInfo)) {
                    //获取到数据后进行解析、存储
                    TasksData data = Parser.toDataEntity(responseInfo, TasksData.class);
                    list.addAll(data.getList());
                    //刷新适配器，把数据展示出来
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                dialog.dismiss();
                ToastUtils.show(TaskActivity.this, "加载失败");
            }
        });

    }

}
