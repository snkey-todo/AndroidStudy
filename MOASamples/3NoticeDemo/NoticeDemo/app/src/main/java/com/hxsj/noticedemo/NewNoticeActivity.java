package com.hxsj.noticedemo;

import com.google.gson.Gson;
import com.hxsj.noticedemo.adapter.GridPhotoAdapter;
import com.hxsj.noticedemo.bean.Attachment;
import com.hxsj.noticedemo.bean.Const;
import com.hxsj.noticedemo.bean.ContactUserInfo;
import com.hxsj.noticedemo.bean.UploadInfo;
import com.hxsj.noticedemo.bean.UserInfo;
import com.hxsj.noticedemo.http.MD5;
import com.hxsj.noticedemo.http.ParamUtils;
import com.hxsj.noticedemo.http.Parser;
import com.hxsj.noticedemo.http.RespEntity;
import com.hxsj.noticedemo.http.UrlUtils;
import com.hxsj.noticedemo.util.Constants;
import com.hxsj.noticedemo.util.DialogUtil;
import com.hxsj.noticedemo.util.PictureUtil;
import com.hxsj.noticedemo.util.ToastUtils;
import com.hxsj.noticedemo.widget.NoScroolGridView;
import com.hxsj.noticedemo.widget.tagview.Tag;
import com.hxsj.noticedemo.widget.tagview.TagCloudLinkView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * 新建通知
 * **/
@ContentView(R.layout.activity_newnotice)
public class NewNoticeActivity extends BaseActivity {

    @ViewInject(R.id.tv_public_title)
    private TextView mtvTitle;  //页面标题
    @ViewInject(R.id.tv_public_send)
    private TextView mtvSend;  //发布按钮
    @ViewInject(R.id.et_newnotice_tittle)
    private EditText mEtTitle;   //新建通知主题
    @ViewInject(R.id.et_newnotice_content)
    private EditText mEtContent;  //新建通知内容
    @ViewInject(R.id.layout_notice_executor)
    private TagCloudLinkView mExecutorCloudLinkView; //新建通知接收人
    @ViewInject(R.id.notice_grid_picture)
    private NoScroolGridView mGridView; //新建通知文件列表
    @ViewInject(R.id.tv_noticecallback)
    private ImageView mNoticeCallback;   //新建通知是否需要回执


    private String title; //通知标题
    private String content; //通知内容
    // 图片资源适配器
    private List<String> mList = new ArrayList<String>();
    private GridPhotoAdapter mAdapter;

    // 图片地址
    private List<UploadInfo> imgUrls = new ArrayList<UploadInfo>();
    private List<ContactUserInfo> mExecuterlist = new ArrayList<ContactUserInfo>();

    //是否需要回执
    private int is_confirm = 0;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        ViewUtils.inject(this);
        //初始化数据
        initdata();
    }

    private void initdata(){
        mtvTitle.setText("通知"); //设置标题
        //设置发布按钮可见，
        mtvSend.setVisibility(View.VISIBLE);
        mtvSend.setText("发布");
        // 执行人标签列表可见
        mExecutorCloudLinkView.drawTags();

        //注册执行标签可以移除的事件函数实现
        mExecutorCloudLinkView.setOnTagDeleteListener(new TagCloudLinkView.OnTagDeleteListener() {

            @Override
            public void onTagDeleted(Tag tag, int position) {
                //执行人标签移除某个人
                mExecuterlist.remove(position);
            }
        });
        // 初始化图片数据模型
        mList.add("add");
        mAdapter = new GridPhotoAdapter(this, mList);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //如果当前点击获取到的数据跟"add"标签匹配，则进行添加操作
                if ("add".equals(mList.get(position))) {

                    //获得当前图片列表的大小，默认需要减去一个"add" 标签
                    int size = mList.size() - 1;
                    //本项目中默认设置最多可以选择的图片张数小于10张，这里获取可以获取的张数
                    if (size<Const.FILE_LIMIT){
                        size=Const.FILE_LIMIT-size;
                    }else
                        return;
                    //带上参数，传递到选择图片的SelectPictureActivity
                    Intent intent = new Intent();
                    intent.setClass(NewNoticeActivity.this, SelectPictureActivity.class);
                    intent.putExtra(SelectPictureActivity.INTENT_MAX_NUM, size);
                    //该函数表明是需要从SelectPitureActivity中返回是需要获取数据的，在onActivityResult() 函数中接收
                    startActivityForResult(intent, Const.REQUEST_PICK);
                }
            }
        });
    }

    @OnClick(R.id.iv_public_back)
    private void onBackClick(View v) {
        finish();
    }

    @OnClick(R.id.tv_newnotice_executor)
    private void setExecutorClick(View v) {
        Intent intent = new Intent();
        //跳转到通讯录模块首页
        intent.setClass(this, ContactsActivity.class);
        //具体项目中，还有很多模块需要从通讯录中选择数据，这边定义通知选人的类型
        intent.putExtra(Const.EXECUTER_TYPE, 1);
        //跳转到通讯录， 在onActivityResult() 函数中接收返回数据
        startActivityForResult(intent, Const.TAG_EXCUTORE);
    }

    @OnClick(R.id.tv_public_send)
    private void onSendClick(View v) {
        // 发送
        title = mEtTitle.getText().toString(); //获取任务标题
        content = mEtContent.getText().toString(); //获取任务的内容
        //通知关键内容的空判断
        if (TextUtils.isEmpty(title)) {
            ToastUtils.show(this, "标题不能为空");
        } else if (TextUtils.isEmpty(content)) {
            ToastUtils.show(this, "任务描述不能为空");
        } else if (mExecuterlist.size() == 0) {
            ToastUtils.show(this, "请选择通知接受人");
        } else {

            dialog = DialogUtil.getprocessDialog(this, "数据提交中");
            dialog.show();
            mtvSend.setEnabled(false);
            // 发布通知
            mList.remove("add");

            //如果通知内容携带着图片内容，先上传图片内容，然后调用发送通知函数
            if (mList.size() > 0) {
                uploadImage(mList.get(0), 0);
            } else {
                addNotice();
            }
        }
    }

    @OnClick(R.id.tv_noticecallback)
    private void onNoticeCallBackClick(View v) {
        //修改回执的数值
        if (is_confirm == 0) {
            is_confirm = 1;
        } else {
            is_confirm = 0;
        }
        //根据回执的状态值，设置回执图片
        mNoticeCallback.setImageResource(is_confirm == 0 ? R.drawable.notice_no_reply : R.drawable.notice_need_reply);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //接收从通讯录中返回的数据
        if (requestCode == Const.TAG_EXCUTORE && resultCode == RESULT_OK) {
            //获取人员临时列表
            List<ContactUserInfo> temp = (List<ContactUserInfo>) data.getSerializableExtra(Const.SELECT_CONTACT);
            //使用hashSet对数据进行重复数据清理工作，首先把界面中已选择的人的Id添加到set中
            HashSet<String> set = new HashSet<String>();
            for (int i = 0; i < mExecuterlist.size(); i++) {
                set.add(mExecuterlist.get(i).getId());
            }
            //去重操作
            for (int i = 0; i < temp.size(); i++) {
                //如果通讯录中的人员id在执行人列表中，不存在，则添加人员到执行人列表中
                if (set.add(temp.get(i).getId())) {
                    mExecuterlist.add(temp.get(i));
                    mExecutorCloudLinkView.add(new Tag(i, temp.get(i).getName()));
                }
            }
            mExecutorCloudLinkView.drawTags();
        } else if (requestCode == Const.REQUEST_PICK && resultCode == RESULT_OK) {
            //接收图片页面返回过来的图片列表，首先移除"add"标签，保证该标签始终在mlist的最后
            mList.remove("add");
            //获取收取到的数据
            List<String> selectedPicture = (ArrayList<String>) data.getSerializableExtra(SelectPictureActivity.INTENT_SELECTED_PICTURE);
            //添加图片地址到图片列表中
            mList.addAll(selectedPicture);
            //保证最多只可以添加9张图片的操作
            if (mList.size() < Const.FILE_LIMIT) {
                mList.add("add");
            }
            // 处理完成后，刷新图片列表
            mAdapter.notifyDataSetChanged();
        }
    }

    private void addNotice() {
        //接口参数方法生成
        ParamUtils paramUtils = new ParamUtils();
        //获取自己用户信息
        UserInfo info = AppLoader.getInstance().getmUserInfo();
        //添加发布通知公告人标识(ID)
        paramUtils.addBizParam("u_id", info.getUser_id());
        //添加发布通知公告人的名字
        paramUtils.addBizParam("u_name", info.getTrue_name());
        //通知公告的标题
        paramUtils.addBizParam("title", title);
        //通知公告的内容
        paramUtils.addBizParam("content", content);
        //通知公告的紧急字段，暂时默认值为0,以后扩展
        paramUtils.addBizParam("important_level", 0);
        //通知公告是否需要回执
        paramUtils.addBizParam("is_confirm", is_confirm);
        //获取通知接收人的集合
        paramUtils.addBizParam("notice_receiver_list", getExecuterList());
        //获取附件的集合
        paramUtils.addBizParam("attachment_list", getAttachMentList());
        //通过UrlUtils.getUrl()方法获取发布通知接口地址
        String url = UrlUtils.getUrl("addnotice");
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configSoTimeout(20000);
        httpUtils.send(HttpMethod.POST, url, paramUtils.getRequestParams(), new RequestCallBack<String>() {

            @SuppressWarnings("rawtypes")
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                dialog.dismiss();
                mtvSend.setEnabled(true);
                if (Parser.isSuccess(responseInfo)) {
                    //请求响应数据解析
                    RespEntity entity = Parser.toRespEntity(responseInfo, String.class);
                    //提示发布的状态
                    ToastUtils.show(getApplicationContext(), entity.getMsg());
                    //发布成功，关闭发布通知页面，返回上一级目录
                    if (entity.getCode() == 0) {
                        finish();
                    }
                }else
                {ToastUtils.show(getApplicationContext(), "发送失败");}
            }

            @Override
            public void onFailure(HttpException error, String msg) {

                mtvSend.setEnabled(true);
                dialog.dismiss();
                if (msg!=null&&"java.net.SocketTimeoutException".equals(msg)){
                    ToastUtils.show(getApplicationContext(), "发送成功");
                    finish();
                }else{
                    ToastUtils.show(getApplicationContext(), "发送失败:响应超时");
                }
            }
        });
    }
    /**
     * 上传文件
     * @param imageFile 图片地址
     * @param  index 上传第几张图片
     */
    private void uploadImage(String imageFile, final int index) {
        //获取当前的时间戳
        long timestamp = System.currentTimeMillis();
        //"UserID=%1$s&PSW=%2$s&Timestamp=%3$s&ID=oa_pic"跟服务端约定的参数封装
        String signSrc = Constants.SIGN_SRC;
        //获取上传地址
        String uploadUrl = UrlUtils.getUploadUrl();
         //通过图片地址获取到图片文件
        File file = PictureUtil.bitmapToFile(imageFile);
        //请求参数封装
        RequestParams params = new RequestParams();
        //添加跟文件服务器约定的上传Userid
        params.addBodyParameter("u", UrlUtils.getUploadUserId());
        //将跟文件服务器约定的协议参数格式化并进行md5加密
        String userid=UrlUtils.getUploadUserId();
        String  secret=UrlUtils.getUploadSecret();
        String format=String.format(signSrc, UrlUtils.getUploadUserId(), UrlUtils.getUploadSecret(), String.valueOf(timestamp));
        params.addBodyParameter("s", MD5.encrypt(String.format(signSrc, UrlUtils.getUploadUserId(), UrlUtils.getUploadSecret(), String.valueOf(timestamp))));
        //添加时间戳
        params.addBodyParameter("t", String.valueOf(timestamp));
        //添加文件参数
        params.addBodyParameter("file", file);
         //获取请求工具类
        HttpUtils http = new HttpUtils();
        //请求发起
        http.send(HttpMethod.POST, uploadUrl, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                //解析请求响应数据
                Gson gson = new Gson();
                UploadInfo upload = gson.fromJson(responseInfo.result, UploadInfo.class);
                if (upload.getState() == 1) {
                    // 上传成功,将返回信息保存
                    imgUrls.add(upload);
                    //如果图片全部上传完成，发送通知
                    if (index == mList.size() - 1) {
                        addNotice();
                        return;
                    }
                } else {
                    // 上传失败
                    ToastUtils.show(NewNoticeActivity.this, "上传失败,index=" + index + Parser.getMsg(responseInfo.result));
                    //如果图片全部上传完成，发送通知
                    if (index == mList.size() - 1) {
                        addNotice();
                    }
                }
                //如果图片全部上传未完成，接着上传下一张图片
                if (index < mList.size() - 1) {
                    uploadImage(mList.get(index + 1), index + 1);
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                //如果图片全部上传未完成，接着上传下一张图片
                if (index < mList.size() - 1) {
                    uploadImage(mList.get(index + 1), index + 1);
                } else {
                    mtvSend.setEnabled(true);
                    dialog.dismiss();
                }
            }
        });
    }

    // 获取抄送人列表
    private JSONArray getExecuterList() {
        try {
            JSONArray list = new JSONArray();
            //读取选择的执行人列表，将列表数据封装成JSONArray格式的数据
            for (ContactUserInfo info : mExecuterlist) {
                JSONObject object = new JSONObject();
                object.put("receiver_id", info.getId());
                object.put("receiver_name", info.getName());
                list.put(object);
            }
            return list;
        } catch (JSONException e) {
        e.printStackTrace();
        }
        return null;
    }

    //获取附件列表
    private JSONArray getAttachMentList() {
        try {
            JSONArray list = new JSONArray();
            //读取选择的执行人列表，将列表数据封装成JSONArray格式的数据
            for (UploadInfo info : imgUrls) {
                JSONObject map = new JSONObject();
                map.put("attach_name", info.getName());
                map.put("attach_suffix", info.getExt());
                map.put("attach_url", info.getUrl());
                list.put(map);
            }
            return list;
        } catch (JSONException e) {
        e.printStackTrace();
        }
        return null;
    }
}

