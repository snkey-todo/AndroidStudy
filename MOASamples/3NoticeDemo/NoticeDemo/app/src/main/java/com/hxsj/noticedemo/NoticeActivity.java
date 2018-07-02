package com.hxsj.noticedemo;

import java.util.ArrayList;
import java.util.List;


import com.hxsj.noticedemo.adapter.NoticeAdapter;
import com.hxsj.noticedemo.bean.Const;
import com.hxsj.noticedemo.bean.Notice;
import com.hxsj.noticedemo.bean.NoticeList;
import com.hxsj.noticedemo.bean.UserInfo;
import com.hxsj.noticedemo.http.ParamUtils;
import com.hxsj.noticedemo.http.Parser;
import com.hxsj.noticedemo.http.UrlUtils;
import com.hxsj.noticedemo.util.DialogUtil;
import com.hxsj.noticedemo.widget.XListView;
import com.hxsj.noticedemo.widget.XListView.IXListViewListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

/**
 * 通知列表页面
 */
@ContentView(R.layout.activity_notice)
public class NoticeActivity extends Activity implements OnItemClickListener, IXListViewListener {

	@ViewInject(R.id.tv_public_title)
	private TextView mTitle;   //标题
	@ViewInject(R.id.lv_notice_listview)
	private XListView mLvNotice;  //通知列表
	@ViewInject(R.id.layout_load_fail)
	private ViewGroup mLoadFailLayout;  //加载失败
	@ViewInject(R.id.tv_empty)
	private TextView mEmptyView; //请求数据为空显示页面
	private NoticeAdapter mAdapter; //列表数据适配器
	private List<Notice> mList = new ArrayList<Notice>(); //通知列表数据集合
	private int page;  //分页请求页码
//	private Dialog dialog;
//	private boolean firsttime=true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		mTitle.setText("通知");
        //初始化数据
		initData();
	}

	@OnClick(R.id.iv_public_back)
	private void onBackClick(View v) {
		finish();
	}

	private void initData() {
		//初始化数据适配器，XListView填充数据
		mAdapter = new NoticeAdapter(this, mList);
		mLvNotice.setAdapter(mAdapter);
		//默认XListView下拉刷新功能打开
		mLvNotice.setPullRefreshEnable(true);
		//默认XListView上拉加载更多关闭
		mLvNotice.setPullLoadEnable(false);
		//设置XListView的点击事件
		mLvNotice.setOnItemClickListener(this);
		//注册XListView的下拉刷新/上拉加载更多接口
		mLvNotice.setXListViewListener(this);
		//请求页码，跟服务端约定的起始值为1
		page = 1;
//		dialog = DialogUtil.getprocessDialog(this, "数据获取中...");
		//请求接口数据
		getNotice(page);
	}

	@SuppressWarnings("static-access")
	private void getNotice(final int index) {
//		if (firsttime) {
//			dialog.show();
//		}
//		mLvNotice.setVisibility(View.VISIBLE);
//		mLoadFailLayout.setVisibility(View.GONE);
//		mEmptyView.setVisibility(View.GONE);
		//调用Http请求工具类
		HttpUtils httpUtils = new HttpUtils();
		//获取登录接口请求成功之后的用户信息
		UserInfo info = AppLoader.getInstance().getmUserInfo();
		//获取接口请求地址,GET请求参数配置方式与POST请求不同，
		String url = UrlUtils.getUrl("getnoticelist", index, Const.PAGE_LIMIT, info.getUser_id(), "");
		//发起数据请求，通知列表数据请求方式为GET
		httpUtils.send(HttpMethod.GET, url, new ParamUtils().getBaseRequestParams(), new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				//数据请求成功，如果是刷新/加载请求，注销掉XListView的请求动作
				stopLoad();
//				if (firsttime) {
//					dialog.dismiss();
//					firsttime=false;
//				}
				//通知列表总的数据集合解析获取
				NoticeList mNoticeList = Parser.toDataEntity(responseInfo, NoticeList.class);
				if (mNoticeList != null) {
					if (index == 1) { //如果是刷新或者初次请求，清空适配器的数据集合，避免数据重复
						mList.clear();
					}
					if (mNoticeList.getList().size() == 0) {
						mEmptyView.setVisibility(View.VISIBLE);
						mEmptyView.setText("暂时没有通知哦");
					}
					//添加数据到数据集合中，刷新数据适配器
					mList.addAll(mNoticeList.getList());
					mAdapter.notifyDataSetChanged();
					//数据请求页码自增加，避免请求到重复的数据
					page++;
					//判断数据列表是否还有下一页数据，如果有在加载更多选项打开，否则关闭
					if (mNoticeList.getHas_next_page() == 1) {
						mLvNotice.setPullLoadEnable(true);
					} else {
						mLvNotice.setPullLoadEnable(false);
					}
				} else {
					mEmptyView.setVisibility(View.VISIBLE);
					mEmptyView.setText("暂时没有通知哦");
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				//数据请求失败，进行对应的界面显示操作
				stopLoad();
//				if (firsttime) {
//					dialog.dismiss();
//					firsttime=false;
//				}
				if (index == 1 && mList.size() == 0) {
					showLoadFailLayout();
				}
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if (position - 1 < 0) {
			return;
		}
//		Intent intent = new Intent();
//		intent.setClass(this, NoticeDetailActivity.class);
//		intent.putExtra(Const.NOTICE_ID, mList.get(position - 1).getNotice_id());
//		intent.putExtra(Const.NOTICE_TIME, mList.get(position - 1).getCreated_time());
		// if (TextUtils.isEmpty(mList.get(position-1).getUrl())) {
		// intent.putExtra(Const.WEB_TYPE, 0);
		//
		// intent.putExtra(Const.WEB_URL, mList.get(position-1).getContent());
		// }else {
		// intent.putExtra(Const.WEB_TYPE, 1);
		// intent.putExtra(Const.WEB_URL, mList.get(position-1).getUrl());
		// }
//		startActivity(intent);
	}

	@OnClick(R.id.layout_reload)
	private void onReloadClick(View v) {
		page = 1;
		getNotice(page);
	}

	private void stopLoad() {
		//停止加载更多，恢复默认状态
		mLvNotice.stopLoadMore();
		//停止数据刷新，恢复默认状态
		mLvNotice.stopRefresh();
	}

	private void showLoadFailLayout() {
		mLoadFailLayout.setVisibility(View.VISIBLE);
		mLvNotice.setVisibility(View.GONE);
		mEmptyView.setVisibility(View.GONE);
	}

	@Override
	public void onRefresh() {
		//重置请求页码
		page = 1;
		getNotice(page);
	}

	@Override
	public void onLoadMore() {
		getNotice(page);
	}
}
