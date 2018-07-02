package com.hxsj.noticedemo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.hxsj.noticedemo.adapter.ContactAdapter;
import com.hxsj.noticedemo.adapter.HorizontalGridViewAdapter;
import com.hxsj.noticedemo.adapter.OrganizationAdapter;
import com.hxsj.noticedemo.bean.Contact;
import com.hxsj.noticedemo.bean.ContactUserData;
import com.hxsj.noticedemo.bean.ContactUserInfo;
import com.hxsj.noticedemo.bean.UserInfo;
import com.hxsj.noticedemo.http.ParamUtils;
import com.hxsj.noticedemo.http.Parser;
import com.hxsj.noticedemo.http.UrlUtils;
import com.hxsj.noticedemo.log.Logger;
import com.hxsj.noticedemo.bean.Const;
import com.hxsj.noticedemo.util.DialogUtil;
import com.hxsj.noticedemo.util.KXDialog;
import com.hxsj.noticedemo.util.KeyBoardUtils;
import com.hxsj.noticedemo.widget.NoScroolListView;
import com.hxsj.noticedemo.util.PinyinUtil;
import com.hxsj.noticedemo.util.ToastUtils;
import com.hxsj.noticedemo.util.Util;
import com.hxsj.noticedemo.widget.XListView;
import com.hxsj.noticedemo.widget.XListView.IXListViewListener;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * 联系人页面
 **/
@ContentView(R.layout.activity_contacts) //加载布局
public class ContactsActivity extends ContactBaseActivity implements OnItemClickListener, IXListViewListener {
	public static final String SELECT_TYPE = "select_type";
	@ViewInject(R.id.iv_contact_back)
	private ImageView mImgBack;  //返回按钮

	@ViewInject(R.id.contactslist)
	private XListView mContactListView;  //通讯录常用联系人列表
	@ViewInject(R.id.searchlist)
	private XListView mSearchlistView;   //通讯录人员搜索列表

	@ViewInject(R.id.tv_contact_sure)
	private TextView mSureView;       //搜索确定按钮
	@ViewInject(R.id.layout_search)
	private RelativeLayout mSearchLayout; //搜索父控件
	@ViewInject(R.id.layout_contact)
	private RelativeLayout mContactLayout;  //
	@ViewInject(R.id.grid)
	private GridView gridview;
	@ViewInject(R.id.ev_search)
	private EditText mevSearch; //搜索输入框
	@ViewInject(R.id.tv_search_cancel)
	private TextView mSearchCancel;
	@ViewInject(R.id.search_layout)
	private RelativeLayout search_layout;
	@ViewInject(R.id.listview)
	private NoScroolListView listview; //通讯录部门列表
	@ViewInject(R.id.no_favourite)
	private TextView no_favourite;
	OrganizationAdapter organizationAdapter;

	List<String> Strlist = new ArrayList<String>();
	//部门数据List
	private List<Contact> mContacts = new ArrayList<Contact>();
	private ContactUserData userData;
	//常用联系人list
	private List<ContactUserInfo> mContactUserInfos = new ArrayList<ContactUserInfo>();
	private ContactAdapter adapter = null;
	private ContactAdapter mSearchAdapter = null;
	//搜索列表list
	private List<ContactUserInfo> mSearchList = new ArrayList<ContactUserInfo>();
	private List<ContactUserInfo> mChoose = new ArrayList<ContactUserInfo>();
	private int type;
	@ViewInject(R.id.indicatorView)
	private TextView mIndicatorView; // 中间显示的提示文本
	@ViewInject(R.id.layout_load_fail)
	private ViewGroup mLoadFailLayout;
	@ViewInject(R.id.tv_empty)
	private TextView mEmptyView;
	@ViewInject(R.id.bottomlayout)
	private FrameLayout bottomlayout;
	@ViewInject(R.id.tv_sortKey)
	private TextView mContactTipsView;
	private Dialog dialog;
	private int mfrom;
	BitmapUtils mBitmapUtils;
	private int page = 1;
	private int search_page = 1;
	private String code;
	private int depth = 1;
	HorizontalGridViewAdapter gridAdapter;
	private Dialog netDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		mfrom = getIntent().getIntExtra(Const.CONTACTS_FROM, 0);
		type = getIntent().getIntExtra(Const.EXECUTER_TYPE, 0);
		dialog = DialogUtil.getprocessDialog(this, "正在加载...");
		mBitmapUtils = Util.getBitmapUtils(this, R.drawable.talk_portrait);
		if (mfrom == 1) {
			mContactListView.setOnItemClickListener(this);
		}
		if (type == 1) {
			bottomlayout.setVisibility(View.VISIBLE);
		}
		mContactListView.setPullLoadEnable(false);
		mContactListView.setPullRefreshEnable(false);
		mContactListView.setXListViewListener(this);
		//设置常用联系人数据适配器
		adapter = new ContactAdapter(mContactUserInfos, ContactsActivity.this, 3, mfrom, 1);
		mContactListView.setAdapter(adapter);
		//设置搜索列表数据适配器
		mSearchAdapter = new ContactAdapter(mSearchList, this, 3, mfrom, 0);
		mSearchlistView.setAdapter(mSearchAdapter);
		mSearchlistView.setPullLoadEnable(false);
		mSearchlistView.setPullRefreshEnable(false);
		mSearchlistView.setXListViewListener(new IXListViewListener() {

			@Override
			public void onLoadMore() {
				String newSearchText = mevSearch.getText().toString().trim();
				if (TextUtils.isEmpty(newSearchText)) {
					ToastUtils.show(ContactsActivity.this, "搜索内容不能为空");
					return;
				}
				getSearchUserInfo(search_page, newSearchText);
			}

			@Override
			public void onRefresh() {

			}
		});
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent();
				intent.setClass(ContactsActivity.this, ContactFirstLevelActivity.class);
				//传递组织机构代码编号
				intent.putExtra("code", mContacts.get(position).getCode());
				//部门名称
				intent.putExtra("title", mContacts.get(position).getName());
				intent.putExtra("Strlist", (Serializable) Strlist);
				intent.putExtra(Const.EXECUTER_TYPE, type);
				intent.putExtra(Const.CONTACTS_FROM, mfrom);
				//选中的联系人列表，
				intent.putExtra("mChoose", (Serializable) mChoose);
				startActivityForResult(intent, Const.ORGANZE_CHOOSE);
			}
		});
		//初始化部门的数据适配器，设置适配器
		organizationAdapter = new OrganizationAdapter(mContacts, ContactsActivity.this);
		listview.setAdapter(organizationAdapter);
		setGridView();
		gridAdapter = new HorizontalGridViewAdapter(mChoose, ContactsActivity.this);
		gridview.setAdapter(gridAdapter);
		gridview.setOnItemClickListener(new ImageClick());
		getContacts();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		page = 1;
		getUserInfo(page);
	}

	/**
	 * 设置GirdView参数，绑定数据
	 */
	@SuppressWarnings("deprecation")
	private void setGridView() {
		int size = mChoose.size();
		int length = 40;
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density;

		int gridviewWidth = (int) (size * (length + 4) * density);
		int itemWidth = (int) (length * density);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
		gridview.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
		gridview.setColumnWidth(itemWidth); // 设置列表项宽
		gridview.setHorizontalSpacing(5); // 设置列表项水平间距
		gridview.setStretchMode(GridView.NO_STRETCH);
		gridview.setNumColumns(size); // 设置列数量=列表集合数
	}

	/**
	 * 显示用户点击的字母索引
	 */
	private Handler mHandler = new Handler();

	private void showIndicatorView(String word) {
		mIndicatorView.setVisibility(View.VISIBLE);
		mIndicatorView.setText(word);
		mHandler.removeCallbacksAndMessages(null);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mIndicatorView.setVisibility(View.GONE);
			}
		}, 1500);
	}

	@OnClick(R.id.iv_contact_back)
	private void setBackClick(View v) {
		if (mSearchLayout.isShown()) {
			search_layout.setVisibility(View.VISIBLE);
			listview.setVisibility(View.VISIBLE);
			mContactListView.setVisibility(View.VISIBLE);
			if (mContactUserInfos.size() > 0) {
				mContactTipsView.setVisibility(View.VISIBLE);
				mEmptyView.setVisibility(View.GONE);
			} else {
				mContactTipsView.setVisibility(View.GONE);
				mEmptyView.setVisibility(View.VISIBLE);
			}
			mevSearch.setText("");
			mSearchLayout.setVisibility(View.GONE);
			mSearchlistView.setVisibility(View.GONE);
			mSearchList.clear();
			mSearchAdapter.notifyDataSetChanged();
			search_page = 1;
			KeyBoardUtils.closeKeybord(mevSearch, this);
		} else {
			//返回，调用finish（）方法，关闭按钮
			finish();
		}
	}

	@OnClick(R.id.tv_search_cancel)
	private void onCancelClick(View v) {
		searchAction();
	}

	@OnClick(R.id.search_layout)
	private void onSearchClick(View v) {
		// 显示搜索视图，隐藏界面中的其他页面元素
		search_layout.setVisibility(View.GONE);
		mContactListView.setVisibility(View.GONE);
		mContactTipsView.setVisibility(View.GONE);
		mEmptyView.setVisibility(View.GONE);
		listview.setVisibility(View.GONE);
		//显示搜索列表跟搜索输入框
		mSearchlistView.setVisibility(View.VISIBLE);
		mSearchLayout.setVisibility(View.VISIBLE);
		//打开键盘
		KeyBoardUtils.openKeybord(mevSearch, this);
		//搜索输入框获取输入的焦点
		mevSearch.setFocusable(true);
		mevSearch.setFocusableInTouchMode(true);
		mevSearch.requestFocus();
		mSearchlistView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mfrom == 1) {
					// 从主菜单进入，查看联系人详情
					position--;
					Intent intent = new Intent();
					intent.setClass(ContactsActivity.this, ContactDetailActivity.class);
					intent.putExtra(Const.ACCOUNT, mSearchList.get(position));
					startActivity(intent);
				} else {
					// 多选状态下执行的操作
					// 搜索列表点击事件
					if (position < 1) {
						return;
					}
					if (type == 2) {
						UserInfo info = AppLoader.getInstance().getmUserInfo();
						if (info.getUser_id().equals(mSearchList.get(position - 1).getId())) {
							ToastUtils.show(ContactsActivity.this, "不能选择自己为审批人");
							return;
						}
					}
					String contactId = mSearchList.get(position - 1).getId();
					if (mSearchList.get(position - 1).mSelect) {
						// 搜索列表取消选中效果
						mSearchList.get(position - 1).mSelect = false;
						mSearchAdapter.notifyDataSetChanged();
						// 取消联系人列表中的选中效果
						for (int i = 0; i < mContactUserInfos.size(); i++) {
							if (contactId.equals(mContactUserInfos.get(i).getId())) {
								mContactUserInfos.get(i).mSelect = false;
								adapter.notifyDataSetChanged();
								break;
							}
						}
						// 去掉底部栏中的头像
						for (int i = 0; i < mChoose.size(); i++) {
							if (contactId.equals(mChoose.get(i).getId())) {
								mChoose.remove(i);
								setGridView();
								gridview.deferNotifyDataSetChanged();
								break;
							}
						}
						if (mChoose.size() != 0) {
							mSureView.setText("确定(" + mChoose.size() + ")");
						} else {
							mSureView.setText("确定");
						}
					} else {
						// 添加搜索列表中的选中效果
						mSearchList.get(position - 1).mSelect = true;
						mSearchAdapter.notifyDataSetChanged();
						// 添加联系人列表中的选中效果
						for (int i = 0; i < mContactUserInfos.size(); i++) {
							if (contactId.equals(mContactUserInfos.get(i).getId())) {
								mContactUserInfos.get(i).mSelect = true;
								adapter.notifyDataSetChanged();
								break;
							}
						}
						// 底部栏添加头像
						mChoose.add(mSearchList.get(position - 1));
						setGridView();
						gridAdapter.notifyDataSetChanged();

						if (mChoose.size() != 0) {
							mSureView.setText("确定(" + mChoose.size() + ")");
						} else {
							mSureView.setText("确定");
						}
					}
					// 选择完成后判断是否是单选，如果是则返回
					if (type == 2) {
						showSuredialog(mChoose.get(0).getName(), mChoose.get(0).getId());
					}
				}
			}
		});
		mevSearch.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_SEARCH)) {
					searchAction();
					return true;
				}
				return false;
			}
		});
	}

	private void showSuredialog(final String name, final String userid) {
		final KXDialog dialog = new KXDialog(this);
		dialog.setCancelable(false);
		dialog.setMessage("您确定添加" + name + "为审批人吗？");
		dialog.setPositiveButton("确定", new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (mChoose.size() > 0) {
					Intent intent2 = new Intent();
					intent2.putExtra(Const.SELECT_CONTACT, (Serializable) mChoose);
					setResult(RESULT_OK, intent2);
				}
				finish();
			}
		}

		);
		dialog.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				restoreData(userid);
			}
		});
		dialog.show();
	}

	private void restoreData(String uid) {
		mChoose.clear();
		if (mSearchList.size() != 0) {
			for (ContactUserInfo info : mSearchList) {
				if (uid.equals(info.getId())) {
					info.mSelect = false;
					break;
				}
			}
			mSearchAdapter.notifyDataSetChanged();
		}
		if (mContactUserInfos.size() != 0) {
			for (ContactUserInfo info : mContactUserInfos) {
				if (uid.equals(info.getId())) {
					info.mSelect = false;
					break;
				}
			}
			adapter.notifyDataSetChanged();
		}
	}

	private void searchAction() {
		// 输入搜索框为空清空之前的数据
		mSearchList.clear();
		//获取搜索输入框输入的关键字，保证输入内容不能为空
		String newSearchText = mevSearch.getText().toString().trim();
		if (TextUtils.isEmpty(newSearchText)) {
			ToastUtils.show(this, "搜索内容不能为空");
			return;
		}
		//调用搜索接口
		getSearchUserInfo(search_page, newSearchText);
	}

	@OnClick(R.id.tv_contact_sure)
	private void onSureClick(View v) {
		Intent intent = new Intent();
		intent.putExtra(Const.SELECT_CONTACT, (Serializable) mChoose);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent();
		intent.setClass(ContactsActivity.this, ContactDetailActivity.class);
		intent.putExtra(Const.ACCOUNT, mContactUserInfos.get(position - 1));
		startActivity(intent);
	}

	@Override
	public void enterPersonDetail(int position) {
		Intent intent = new Intent();
		intent.setClass(ContactsActivity.this, ContactDetailActivity.class);
		if (mSearchLayout.isShown()) {
			intent.putExtra(Const.ACCOUNT, mSearchList.get(position));
		} else {
			intent.putExtra(Const.ACCOUNT, mContactUserInfos.get(position));
		}
		startActivity(intent);
	}

	public void updateUserInfo(ContactUserInfo contactUserInfo) {
		super.updateUserInfo(contactUserInfo);
		Logger.getLogger().d("contactsActivity  update");
		for (int i = 0; i < mSearchList.size(); i++) {
			if (contactUserInfo.getId().equals(mSearchList.get(i).getId())) {
				mSearchList.get(i).setIs_collect(contactUserInfo.getIs_collect());
				return;
			}
		}
		if (contactUserInfo.getIs_collect() == 1) {
			if (mSearchLayout.isShown()) {
				mContactTipsView.setVisibility(View.VISIBLE);
			}
			mContactUserInfos.add(contactUserInfo);
			adapter.notifyDataSetChanged();
		} else {
			for (int i = 0; i < mContactUserInfos.size(); i++) {
				if (contactUserInfo.getId().equals(mContactUserInfos.get(i).getId())) {
					mContactUserInfos.remove(i);
					adapter.notifyDataSetChanged();
					if (mContactUserInfos.size() == 0) {
						mContactTipsView.setVisibility(View.GONE);
						mEmptyView.setVisibility(View.VISIBLE);
					}
					return;
				}
			}
		}

	}


	/**
	 * 联系人列表点击选人操作
	 * 
	 **/
	public void choosePerson(int position) {
		if (type == 2) {
			UserInfo info = AppLoader.getInstance().getmUserInfo();
			if (info.getUser_id().equals(mContactUserInfos.get(position).getId())) {
				ToastUtils.show(ContactsActivity.this, "不能选择自己为审批人");
				return;
			}
		}
		if (mContactUserInfos.get(position).mSelect) {
			// 联系人列表取消选中状态
			mContactUserInfos.get(position).mSelect = false;
			adapter.notifyDataSetChanged();
			// 底部栏去掉头像
			for (int i = 0; i < mChoose.size(); i++) {
				if (mContactUserInfos.get(position).getId().equals(mChoose.get(i).getId())) {
					mChoose.remove(i);
					setGridView();
					gridAdapter.notifyDataSetChanged();
					break;
				}
			}
			if (mChoose.size() != 0) {
				mSureView.setText("确定(" + mChoose.size() + ")");
			} else {
				mSureView.setText("确定");
			}
		} else {
			// 联系人列表添加选中状态
			mContactUserInfos.get(position).mSelect = true;
			adapter.notifyDataSetChanged();
			// 底部栏添加头像
			mChoose.add(mContactUserInfos.get(position));
			setGridView();
			gridAdapter.notifyDataSetChanged();
			if (mChoose.size() != 0) {
				mSureView.setText("确定(" + mChoose.size() + ")");
			} else {
				mSureView.setText("确定");
			}
		}
		// 选择完成后判断是否是单选，如果是则返回
		if (type == 2) {
			showSuredialog(mChoose.get(0).getName(), mChoose.get(0).getId());
		}
	}

	/**
	 * 底部栏头像点击事件
	 * 
	 * @author Administrator
	 * 
	 */
	public class ImageClick implements OnItemClickListener {

		String contactId;

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			contactId = mChoose.get(position).getId();
			// 联系人列表取消选中状态
			for (int i = 0; i < mContactUserInfos.size(); i++) {
				if (contactId.equals(mContactUserInfos.get(i).getId())) {
					mContactUserInfos.get(i).mSelect = false;
					adapter.notifyDataSetChanged();
					break;
				}
			}
			// 搜索列表取消选中状态
			for (int i = 0; i < mSearchList.size(); i++) {
				if (contactId.equals(mSearchList.get(i).getId())) {
					mSearchList.get(i).mSelect = false;
					mSearchAdapter.notifyDataSetChanged();
					break;
				}
			}
			// 底部栏去掉头像
			mChoose.remove(position);
			setGridView();
			gridAdapter.notifyDataSetChanged();
			if (mChoose.size() != 0) {
				mSureView.setText("确定(" + mChoose.size() + ")");
			} else {
				mSureView.setText("确定");
			}
		}

	}

	/**
	 * 搜索联系人列表
	 */
	@SuppressWarnings("static-access")
	private void getSearchUserInfo(int index, String keywords) {
		//搜索请求进度对话框
		dialog.show();
		//封装接口请求工具
		HttpUtils httpUtils = new HttpUtils();
		//获取接口请求地址，接口中各参数的意义请参考通讯录列表接口中的文档定义
		String url = UrlUtils.getUrl("getcontactlistsearch", index, 30, code, depth, keywords, AppLoader.getInstance().getmUserInfo().getUser_id()) + "&only=false";
		//发送请求数据请求
		httpUtils.send(HttpMethod.GET, url, new ParamUtils().getBaseRequestParams(), new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				//数据请求成功，进度框消失
				dialog.dismiss();
				//判断返回的数据是否能成功解析
				if (Parser.isSuccess(responseInfo)) {
					//按照模板类定义解析数据
					ContactUserData searchdata = Parser.toDataEntity(responseInfo, ContactUserData.class);
					//填充数据集合列表
					mSearchList.addAll(searchdata.getList());
					if (mSearchList.size() == 0) {
						ToastUtils.show(ContactsActivity.this, "没有搜索到相关联系人");
					}
					// 如果在搜索之前已经选择有联系人，那么在搜索出来的列表中设置该联系人为选中状态
					for (int i = 0; i < mChoose.size(); i++) {
						for (int j = 0; j < mSearchList.size(); j++) {
							if (mChoose.get(i).getId().equals(mSearchList.get(j).getId())) {
								mSearchList.get(j).mSelect = true;
							}
						}
					}
					//刷新搜索数据列表
					mSearchAdapter.notifyDataSetChanged();
					//搜索列表是否可以拉取更多数据的控制开关
					if (searchdata.getHas_next_page() > 0) {
						mSearchlistView.setPullLoadEnable(true);
						search_page++;
					} else {
						mSearchlistView.setPullLoadEnable(false);
					}
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				dialog.dismiss();
				if (netDialog != null && !netDialog.isShowing()) {
					netDialog.show();
				}
			}
		});
	}

	/**
	 * 获取通讯录首页联系人列表
	 */
	@SuppressWarnings("static-access")
	private void getUserInfo(int index) {
		if (page == 1) {
			dialog.show();
		}
		mLoadFailLayout.setVisibility(View.GONE);
		mSearchlistView.setVisibility(View.GONE);
		mEmptyView.setVisibility(View.GONE);
		//获取请求封装工具类
		HttpUtils httpUtils = new HttpUtils();
		//获取请求的接口，该接口配置了分页加载，需要传递请求用户的id,请求页码，最大一页请求数据
		String url = UrlUtils.getUrl("getfavourite", AppLoader.getInstance().getmUserInfo().getUser_id(), index, 30);
		//发送数据请求，获取常用联系人列表
		httpUtils.send(HttpMethod.GET, url, new ParamUtils().getBaseRequestParams(), new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				if (Parser.isSuccess(responseInfo)) {
					//数据解析成功，将请求的数据映射到对应的数据模型中
					userData = Parser.toDataEntity(responseInfo, ContactUserData.class);
					//清空常用联系数据列表
					if (page == 1) {
						mContactUserInfos.clear();
					}
					//添加常用人数据
					mContactUserInfos.addAll(userData.getList());
					if (mContactUserInfos.size() == 0) {
						mEmptyView.setVisibility(View.VISIBLE);
						mEmptyView.setText("暂无常用联系人,赶紧去添加吧 !");
					} else {
						mContactLayout.setVisibility(View.VISIBLE);
						mContactTipsView.setVisibility(View.VISIBLE);
						mContactListView.setVisibility(View.VISIBLE);
					}
					//将人物名称转成拼音，方便按照名称排序
					for (int i = 0; i < mContactUserInfos.size(); i++) {
						String pinyin = mContactUserInfos.get(i).getName();
						//调用pinyin转换库，将汉字的名字转换成拼音
						mContactUserInfos.get(i).setPingyin(PinyinUtil.getPinyin(pinyin));
					}
					//更新常用联系人数据列表
					adapter.notifyDataSetChanged();
					//数据列表是否可加载更多
					if (userData.getHas_next_page() > 0) {
						mContactListView.setPullLoadEnable(true);
						page++;
					} else {
						mContactListView.setPullLoadEnable(false);
					}
					//数据加载完成
					stopLoad();
					if (page == 1 && dialog != null) {
						dialog.dismiss();
					}
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				if (page == 1 && dialog != null) {
					dialog.dismiss();
				}
				stopLoad();
				showLoadFailLayout();
			}
		});
	}

	private void stopLoad() {
		mContactListView.stopLoadMore();
	}

	/**
	 * 获取顶级组织列表
	 */

	@SuppressWarnings("static-access")
	private void getContacts() {
		if (page == 1) {
			dialog.show();
		}
		//获取接口请求的地址
		String url = UrlUtils.getUrl("getcontacts");
		//初始化请求工具类
		HttpUtils httpUtils = new HttpUtils();
        //封装相关的参数，调用接口请求，等待接口响应
		httpUtils.send(HttpMethod.GET, url, new ParamUtils().getBaseRequestParams(), new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				//数据请求成功，数据解析封装
				if (Parser.isSuccess(responseInfo)) {
					//清空通讯录列表，添加数据
					mContacts.clear();
					mContacts.addAll(Parser.toListEntity(responseInfo, Contact.class));
					if (mContacts.size() == 0) {
						mEmptyView.setVisibility(View.VISIBLE);
						mEmptyView.setText("没有联系人");
					} else {
						//设置通讯录部门可见
						mContactLayout.setVisibility(View.VISIBLE);
						//初始化组织结构代码，code跳转下一个页面时候需要传递该参数
						code = mContacts.get(0).getCode();
						//刷新部门列表
						organizationAdapter.notifyDataSetChanged();
					}
				} else {
					ToastUtils.show(ContactsActivity.this, Parser.getMsg(responseInfo.result));
				}
				if (page == 1 && dialog != null) {
					dialog.dismiss();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				if (page == 1 && dialog != null) {
					dialog.dismiss();
				}
				if (netDialog != null && !netDialog.isShowing()) {
					netDialog.show();
				}
			}
		});

	}

	/**
	 * 加载失败，重新加载
	 * 
	 * @param v
	 */

	@OnClick(R.id.layout_reload)
	private void onReloadClick(View v) {
		getUserInfo(page);
	}

	private void showLoadFailLayout() {
		mLoadFailLayout.setVisibility(View.VISIBLE);
		mEmptyView.setVisibility(View.GONE);
	}


	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 单选带回的数据
		if (requestCode == Const.SELECT_CONTACT_TYPE && resultCode == RESULT_OK) {
			Intent intent = new Intent();
			intent.putExtra(Const.SELECT_CONTACT, data.getSerializableExtra(Const.SELECT_CONTACT));
			setResult(RESULT_OK, intent);
			finish();
		}
		if (requestCode == Const.ORGANZE_CHOOSE && resultCode == RESULT_OK) {
			// 从组织架构中选择人物
			List<ContactUserInfo> infos = (List<ContactUserInfo>) data.getSerializableExtra(Const.SELECT_CONTACT);
			// 判断数据是否在选择列表中存在
			mChoose.addAll(infos);
			if (type == 2) {
				if (mChoose.size() > 0) {
					Intent intent2 = new Intent();
					intent2.putExtra(Const.SELECT_CONTACT, (Serializable) mChoose);
					setResult(RESULT_OK, intent2);
				}
				finish();
			}
		}
		if (requestCode == Const.ORGANZE_CHOOSE && resultCode == 10) {
			// 多选
			Intent intent = new Intent();
			intent.putExtra(Const.SELECT_CONTACT, data.getSerializableExtra(Const.MCHOOSE));
			setResult(RESULT_OK, intent);
			finish();
		}
		if (requestCode == Const.ORGANZE_CHOOSE && resultCode == 20) {
			// 传递底部栏数据
			mChoose.clear();
			mChoose = (List<ContactUserInfo>) data.getSerializableExtra(Const.MCHOOSE);
			setGridView();
			gridAdapter = new HorizontalGridViewAdapter(mChoose, ContactsActivity.this);
			gridview.setAdapter(gridAdapter);
			if (mChoose.size() != 0) {
				mSureView.setText("确定(" + mChoose.size() + ")");
			} else {
				mSureView.setText("确定");
			}
			for (int j = 0; j < mContactUserInfos.size(); j++) {
				mContactUserInfos.get(j).mSelect = false;
			}
			for (int i = 0; i < mChoose.size(); i++) {
				for (int j = 0; j < mContactUserInfos.size(); j++) {
					if (mChoose.get(i).getId().equals(mContactUserInfos.get(j).getId())) {
						mContactUserInfos.get(j).mSelect = true;
					}
				}
			}
			adapter.notifyDataSetChanged();
		}

	}

	@Override
	public void onRefresh() {

	}

	/**
	 * 加载更多
	 */
	@Override
	public void onLoadMore() {
		getUserInfo(page);
	}

}
