package com.hxsj.noticedemo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.hxsj.noticedemo.adapter.ContactAdapter;
import com.hxsj.noticedemo.adapter.ContactFirstLevelAdapter;
import com.hxsj.noticedemo.adapter.ContactThirdLevelAdapter;
import com.hxsj.noticedemo.adapter.HorizontalGridViewAdapter;
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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

@ContentView(R.layout.activity_contact_firstlevel)
public class ContactFirstLevelActivity extends ContactBaseActivity implements OnItemClickListener {

	@ViewInject(R.id.iv_public_back)
	private ImageView back;
	@ViewInject(R.id.tv_public_title)
	private TextView title;
	@ViewInject(R.id.listview)
	private NoScroolListView listview;
	@ViewInject(R.id.listview_contacts)
	private NoScroolListView listview_contacts;
	@ViewInject(R.id.bottomlayout)
	private FrameLayout bottom_layout;
	@ViewInject(R.id.tv_contact_sure)
	private TextView mSureView;

	// @ViewInject(R.id.iv_operate)
	// private ImageView mOperate;
	@ViewInject(R.id.layout_search)
	private RelativeLayout mSearchLayout;
	@ViewInject(R.id.ev_search)
	private EditText mEdtsearch;
	@ViewInject(R.id.tv_search_cancel)
	private TextView mStartSearch;
	@ViewInject(R.id.searchlist)
	private XListView mSearchlistView;
	@ViewInject(R.id.scrollview_layout)
	private ScrollView mScrollView;
	@ViewInject(R.id.grid)
	private GridView gridview;
	@ViewInject(R.id.search_layout)
	private RelativeLayout search_layout;

	List<String> Strlist = new ArrayList<>();
	List<Contact> list = new ArrayList<Contact>();
	ContactUserData userData;
	List<ContactUserInfo> mContactUserInfos = new ArrayList<ContactUserInfo>();
	List<ContactUserInfo> mChoose = new ArrayList<ContactUserInfo>();

	List<ContactUserInfo> mSearchList = new ArrayList<ContactUserInfo>();
	ContactAdapter mSearchAdapter;

	ContactFirstLevelAdapter organizationAdapter;
	ContactThirdLevelAdapter contactThirdLevelAdapter;
	Context context = this;
	Dialog dialog;
	private boolean f1 = false, f2 = false;// 标识两个接口访问数据请求是否完成
	String code;
	private int type;
	private int mFrom;
	BitmapUtils mBitmapUtils;
	String titleStr;
	private int page = 1;
	private int search_page = 1;
	private int depth;
	HorizontalGridViewAdapter gridAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		init();
		getContactsFirstLevel();
		// 如果是第一次进入到这个页面那么depth=2,否则自动加一
		if (getIntent().getIntExtra("depth", 0) == 1) {
			depth = 2;
			getContactList(depth);
		} else {
			depth = getIntent().getIntExtra("depth", 1) + 1;
			getContactList(depth);
		}
	}

	@SuppressWarnings("unchecked")
	private void init() {
		// mOperate.setImageResource(R.drawable.search);
		// mOperate.setVisibility(View.VISIBLE);
		mStartSearch.setText("搜索");

		dialog = DialogUtil.getprocessDialog(ContactFirstLevelActivity.this, "正在加载...");
		title.setText(getIntent().getStringExtra("title"));
		Strlist = (List<String>) getIntent().getSerializableExtra("Strlist");
		Strlist.add(getIntent().getStringExtra("title"));
		code = getIntent().getStringExtra("code");
		type = getIntent().getIntExtra(Const.EXECUTER_TYPE, 0);
		mFrom = getIntent().getIntExtra(Const.CONTACTS_FROM, 0);
		mChoose = (List<ContactUserInfo>) getIntent().getSerializableExtra("mChoose");
		listview.setOnItemClickListener(this);
		mBitmapUtils = Util.getBitmapUtils(this, R.drawable.talk_portrait);

		mSearchAdapter = new ContactAdapter(mSearchList, this, 3, mFrom, 0);
		mSearchlistView.setAdapter(mSearchAdapter);
		mSearchlistView.setPullLoadEnable(false);
		mSearchlistView.setPullRefreshEnable(false);
		mSearchlistView.setOnItemClickListener(new MySearchListener());
		mSearchlistView.setXListViewListener(new IXListViewListener() {

			@Override
			public void onLoadMore() {
				String newSearchText = mEdtsearch.getText().toString().trim();
				if (TextUtils.isEmpty(newSearchText)) {
					ToastUtils.show(ContactFirstLevelActivity.this, "搜索内容不能为空");
					return;
				}
				getSearchUserInfo(search_page, newSearchText);
			}

			@Override
			public void onRefresh() {

			}
		});

		if (type == 1) {
			bottom_layout.setVisibility(View.VISIBLE);
		}
		organizationAdapter = new ContactFirstLevelAdapter(list, context);
		listview.setAdapter(organizationAdapter);
		contactThirdLevelAdapter = new ContactThirdLevelAdapter(mContactUserInfos, ContactFirstLevelActivity.this, mFrom);
		listview_contacts.setAdapter(contactThirdLevelAdapter);
		setGridView();
		gridAdapter = new HorizontalGridViewAdapter(mChoose, ContactFirstLevelActivity.this);
		gridview.setAdapter(gridAdapter);
		gridview.setOnItemClickListener(new ImageClick());
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
					contactThirdLevelAdapter.notifyDataSetChanged();
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

	class MySearchListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			if (mFrom == 1) {
				// 从主菜单进入，查看联系人详情
				position--;
				Intent intent = new Intent();
				intent.setClass(ContactFirstLevelActivity.this, ContactDetailActivity.class);
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
						ToastUtils.show(ContactFirstLevelActivity.this, "不能选择自己为审批人");
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
							contactThirdLevelAdapter.notifyDataSetChanged();
							break;
						}
					}
					// 去掉底部栏中的头像
					for (int i = 0; i < mChoose.size(); i++) {
						if (contactId.equals(mChoose.get(i).getId())) {
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
					// 添加搜索列表中的选中效果
					mSearchList.get(position - 1).mSelect = true;
					mSearchAdapter.notifyDataSetChanged();
					// 添加联系人列表中的选中效果
					for (int i = 0; i < mContactUserInfos.size(); i++) {
						if (contactId.equals(mContactUserInfos.get(i).getId())) {
							mContactUserInfos.get(i).mSelect = true;
							contactThirdLevelAdapter.notifyDataSetChanged();
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
					// if (mChoose.size() > 0) {
					// Intent intent2 = new Intent();
					// intent2.putExtra(Const.SELECT_CONTACT, (Serializable)
					// mChoose);
					// setResult(RESULT_OK, intent2);
					// }
					// finish();
					showSuredialog(mChoose.get(0).getName(), mChoose.get(0).getId());
				}
			}

		}
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
			contactThirdLevelAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 点击搜索按钮触发事件
	 * 
	 * @param v
	 *            View
	 */
	@OnClick(R.id.search_layout)
	private void onOperateClick(View v) {
		search_layout.setVisibility(View.GONE);
		mSearchLayout.setVisibility(View.VISIBLE);
		mSearchlistView.setVisibility(View.VISIBLE);
		mScrollView.setVisibility(View.GONE);
		KeyBoardUtils.openKeybord(mEdtsearch, this);
		mEdtsearch.setFocusable(true);
		mEdtsearch.setFocusableInTouchMode(true);
		mEdtsearch.requestFocus();
	}

	@OnClick(R.id.tv_search_cancel)
	private void onCancelClick(View v) {
		searchAction();
	}

	private void searchAction() {
		// 输入搜索框为空清空之前的数据
		// mSearchList.clear();
		String newSearchText = mEdtsearch.getText().toString().trim();
		if (TextUtils.isEmpty(newSearchText)) {
			ToastUtils.show(this, "搜索内容不能为空");
			return;
		}
		getSearchUserInfo(search_page, newSearchText);
	}

	/**
	 * 点击返回按钮触发事件
	 * 
	 * @param v
	 *            View
	 */
	@OnClick(R.id.iv_public_back)
	private void onBackClick(View v) {
		if (mSearchLayout.isShown()) {
			search_layout.setVisibility(View.VISIBLE);
			mScrollView.setVisibility(View.VISIBLE);
			mEdtsearch.setText("");
			mSearchLayout.setVisibility(View.GONE);
			mSearchlistView.setVisibility(View.GONE);
			mSearchList.clear();
			mSearchAdapter.notifyDataSetChanged();
			search_page = 1;
			KeyBoardUtils.closeKeybord(mEdtsearch, this);
		} else {
			Intent intent = new Intent();
			intent.putExtra(Const.MCHOOSE, (Serializable) mChoose);
			setResult(20, intent);
			finish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent();
		if (!list.get(position).getAllow_next()) {
			intent.setClass(ContactFirstLevelActivity.this, ContactThirdLevelActivity.class);
			intent.putExtra("code", list.get(position).getCode());
			intent.putExtra("depth", list.get(position).getDepth() + "");
			intent.putExtra("mChoose", (Serializable) mChoose);
			intent.putExtra(Const.EXECUTER_TYPE, type);
			intent.putExtra("Strlist", (Serializable) Strlist);
			intent.putExtra("title", list.get(position).getName());
			intent.putExtra("org", getIntent().getStringExtra("org"));
		} else {
			if (list.get(position).getIs_last() && list.get(position).getCount() != 0) {
				intent.setClass(ContactFirstLevelActivity.this, ContactThirdLevelActivity.class);
				intent.putExtra("code", list.get(position).getCode());
				intent.putExtra("depth", list.get(position).getDepth() + "");
				intent.putExtra("title", list.get(position).getName());
				intent.putExtra("mChoose", (Serializable) mChoose);
				intent.putExtra(Const.EXECUTER_TYPE, type);
				intent.putExtra("Strlist", (Serializable) Strlist);
				intent.putExtra("org", getIntent().getStringExtra("org"));
			} else {
				intent.setClass(ContactFirstLevelActivity.this, ContactFirstLevelActivity.class);
				intent.putExtra("code", list.get(position).getCode());
				intent.putExtra("title", list.get(position).getName());
				intent.putExtra("depth", depth);
				intent.putExtra("mChoose", (Serializable) mChoose);
				intent.putExtra(Const.EXECUTER_TYPE, type);
				intent.putExtra("Strlist", (Serializable) Strlist);
				intent.putExtra("org", getIntent().getStringExtra("title"));
			}
		}
		intent.putExtra(Const.CONTACTS_FROM, mFrom);
		// mChoose.clear();
		startActivityForResult(intent, Const.SELECT_CONTACT_TYPE);
	}

	/**
	 * 获取当前目录列表
	 */
	@SuppressWarnings("static-access")
	private void getContactsFirstLevel() {
		if (!dialog.isShowing()) {
			dialog.show();
			f1 = false;
		}
		String url = UrlUtils.getUrl("getcontacts") + "?code=" + code;
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.GET, url, new ParamUtils().getBaseRequestParams(), new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				f1 = true;
				if (f2 && dialog.isShowing()) {
					dialog.dismiss();
				}
				if (Parser.isSuccess(responseInfo)) {
					list.clear();
					list.addAll(Parser.toListEntity(responseInfo, Contact.class));
					organizationAdapter.notifyDataSetChanged();
					// mEmptyView.setVisibility(View.VISIBLE);
					// mEmptyView.setText("暂时没有内容");
				} else {
					ToastUtils.show(ContactFirstLevelActivity.this, Parser.getMsg(responseInfo.result));
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				f1 = true;
				if (f2 && dialog.isShowing()) {
					dialog.dismiss();
				}
				ToastUtils.show(ContactFirstLevelActivity.this, msg);
			}
		});
	}

	/**
	 * 获取当前目录下的联系人列表
	 * 
	 * @param depth
	 *            int
	 */

	@SuppressWarnings("static-access")
	private void getContactList(int depth) {
		if (!dialog.isShowing()) {
			dialog.show();
			f2 = false;
		}
		String url = UrlUtils.getUrl("getcontactlist", page, 30, code, depth, AppLoader.getInstance().getmUserInfo().getUser_id()) + "&only=true";
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.GET, url, new ParamUtils().getBaseRequestParams(), new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				f2 = true;
				if (f1 && dialog.isShowing()) {
					dialog.dismiss();
				}
				if (Parser.isSuccess(responseInfo)) {
					userData = Parser.toDataEntity(responseInfo, ContactUserData.class);
					if (userData != null) {
						mContactUserInfos.addAll(userData.getList());
						selectmChoosedPerson();
						contactThirdLevelAdapter.notifyDataSetChanged();
					}
					listview_contacts.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							if (mFrom == 1) {
								Intent intent = new Intent();
								intent.setClass(ContactFirstLevelActivity.this, ContactDetailActivity.class);
								intent.putExtra(Const.ACCOUNT, mContactUserInfos.get(position));
								startActivity(intent);
							}
						}
					});
				} else {
					ToastUtils.show(ContactFirstLevelActivity.this, Parser.getMsg(responseInfo.result));
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				f2 = true;
				if (f1 && dialog.isShowing()) {
					dialog.dismiss();
				}
				ToastUtils.show(ContactFirstLevelActivity.this, msg);
			}
		});
	}

	// 如果底部已选中的人员存在于当前页面列表中，则设定为选中状态
	private void selectmChoosedPerson() {
		for (int i = 0; i < mChoose.size(); i++) {
			for (int j = 0; j < mContactUserInfos.size(); j++) {
				if (mChoose.get(i).getId().equals(mContactUserInfos.get(j).getId())) {
					mContactUserInfos.get(j).mSelect = true;
				}
			}
		}
		if (mChoose.size() != 0) {
			mSureView.setText("确定(" + mChoose.size() + ")");
		} else {
			mSureView.setText("确定");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Const.SELECT_CONTACT_TYPE && resultCode == RESULT_OK) {
			Intent intent = new Intent();
			intent.putExtra(Const.SELECT_CONTACT, data.getSerializableExtra(Const.SELECT_CONTACT));
			setResult(RESULT_OK, intent);
			finish();
		}
		if (requestCode == Const.SELECT_CONTACT_TYPE && resultCode == 10) {
			Intent intent = new Intent();
			intent.putExtra(Const.MCHOOSE, data.getSerializableExtra(Const.MCHOOSE));
			setResult(10, intent);
			finish();
		}
		if (requestCode == Const.SELECT_CONTACT_TYPE && resultCode == 20) {
			mChoose.clear();
			mChoose = (List<ContactUserInfo>) data.getSerializableExtra(Const.MCHOOSE);
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
			contactThirdLevelAdapter.notifyDataSetChanged();
			setGridView();
			gridAdapter = new HorizontalGridViewAdapter(mChoose, ContactFirstLevelActivity.this);
			gridview.setAdapter(gridAdapter);
			if (mChoose.size() != 0) {
				mSureView.setText("确定(" + mChoose.size() + ")");
			} else {
				mSureView.setText("确定");
			}
		}
		if (requestCode == Const.SELECT_CONTACT_TYPE && resultCode == 30) {
			String titleStr = data.getStringExtra("titleStr");
			if (Strlist.contains(titleStr) && Strlist.get(Strlist.size() - 1).equals(titleStr)) {
				return;
			} else {
				Intent intent = new Intent();
				intent.putExtra("titleStr", titleStr);
				setResult(30, intent);
				finish();
			}
		}

	}

	@OnClick(R.id.tv_contact_sure)
	private void onSureClick(View v) {
		if (mChoose.size() > 0) {
			Intent intent = new Intent();
			intent.putExtra(Const.MCHOOSE, (Serializable) mChoose);
			setResult(10, intent);
		}
		finish();
	}

	@Override
	public void choosePerson(int position) {
		if (type == 2) {
			UserInfo info = AppLoader.getInstance().getmUserInfo();
			if (info.getUser_id().equals(mContactUserInfos.get(position).getId())) {
				ToastUtils.show(ContactFirstLevelActivity.this, "不能选择自己为审批人");
				return;
			}
		}
		if (mContactUserInfos.get(position).mSelect) {
			// 联系人列表取消选中状态
			// mChoose.remove(mContactUserInfos.get(position));
			// setGridView();
			// gridAdapter.notifyDataSetChanged();

			mContactUserInfos.get(position).mSelect = false;
			contactThirdLevelAdapter.notifyDataSetChanged();
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
			contactThirdLevelAdapter.notifyDataSetChanged();
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
			// if (mChoose.size() > 0) {
			// Intent intent2 = new Intent();
			// intent2.putExtra(Const.SELECT_CONTACT, (Serializable) mChoose);
			// setResult(RESULT_OK, intent2);
			// }
			// finish();
			showSuredialog(mChoose.get(0).getName(), mChoose.get(0).getId());
		}
	}

	@Override
	public void enterPersonDetail(int position) {
		Intent intent = new Intent();
		intent.setClass(ContactFirstLevelActivity.this, ContactDetailActivity.class);

		if (mSearchLayout.isShown()) {
			intent.putExtra(Const.ACCOUNT, mSearchList.get(position));
		} else {
			intent.putExtra(Const.ACCOUNT, mContactUserInfos.get(position));
		}
		startActivity(intent);
	}

	@Override
	public void updateUserInfo(ContactUserInfo contactUserInfo) {
		super.updateUserInfo(contactUserInfo);
		Logger.getLogger().d("contactsFirstActivity  update");
		if (mSearchLayout.isShown()) {
			for (int i = 0; i < mSearchList.size(); i++) {
				if (contactUserInfo.getId().equals(mSearchList.get(i).getId())) {
					mSearchList.get(i).setIs_collect(contactUserInfo.getIs_collect());
					return;
				}
			}
		}
		for (int i = 0; i < mContactUserInfos.size(); i++) {
			if (contactUserInfo.getId().equals(mContactUserInfos.get(i).getId())) {
				mContactUserInfos.get(i).setIs_collect(contactUserInfo.getIs_collect());
				return;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if (mChoose.size() > 0) {
				Intent intent = new Intent();
				intent.putExtra(Const.MCHOOSE, (Serializable) mChoose);
				setResult(20, intent);
			}
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 搜索联系人列表
	 */
	@SuppressWarnings("static-access")
	private void getSearchUserInfo(int index, String keywords) {
		dialog.show();
		HttpUtils httpUtils = new HttpUtils();
		String url = UrlUtils.getUrl("getcontactlistsearch", index, 30, code, 1, keywords, AppLoader.getInstance().getmUserInfo().getUser_id()) + "&only=false";
		httpUtils.send(HttpMethod.GET, url, new ParamUtils().getBaseRequestParams(), new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				dialog.dismiss();
				if (Parser.isSuccess(responseInfo)) {
					ContactUserData searchdata = Parser.toDataEntity(responseInfo, ContactUserData.class);
					mSearchList.clear();
					mSearchList.addAll(searchdata.getList());
					if (mSearchList.size() == 0) {
						ToastUtils.show(ContactFirstLevelActivity.this, "没有搜索到相关联系人");
					}
					// 如果在搜索之前已经选择有联系人，那么在搜索出来的列表中设置该联系人为选中状态
					for (int i = 0; i < mChoose.size(); i++) {
						for (int j = 0; j < mSearchList.size(); j++) {
							if (mChoose.get(i).getId().equals(mSearchList.get(j).getId())) {
								mSearchList.get(j).mSelect = true;
							}
						}
					}
					mSearchAdapter.notifyDataSetChanged();
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
				ToastUtils.show(ContactFirstLevelActivity.this, msg);
			}
		});
	}

}
