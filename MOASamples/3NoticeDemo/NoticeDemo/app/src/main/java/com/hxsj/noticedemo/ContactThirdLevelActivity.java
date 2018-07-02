package com.hxsj.noticedemo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.hxsj.noticedemo.adapter.ContactAdapter;
import com.hxsj.noticedemo.adapter.ContactThirdLevelAdapter;
import com.hxsj.noticedemo.adapter.HorizontalGridViewAdapter;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@ContentView(R.layout.activity_contact_thirdlevel)
public class ContactThirdLevelActivity extends ContactBaseActivity implements OnItemClickListener, IXListViewListener {

	@ViewInject(R.id.iv_public_back)
	private ImageView back;
	@ViewInject(R.id.tv_public_title)
	private TextView title;
	@ViewInject(R.id.listview)
	private XListView listview;
	@ViewInject(R.id.bottomlayout)
	private FrameLayout bottom_layout;
	@ViewInject(R.id.tv_contact_sure)
	private TextView mSureView;
	@ViewInject(R.id.Horizontal_layout)
	private LinearLayout Horizontal_layout;
	@ViewInject(R.id.mHorizontalScrollView)
	private HorizontalScrollView mHorizontalScrollView;

	@ViewInject(R.id.iv_operate)
	private ImageView mOperate;
	@ViewInject(R.id.layout_search)
	private RelativeLayout mSearchLayout;
	@ViewInject(R.id.ev_search)
	private EditText mEdtsearch;
	@ViewInject(R.id.tv_search_cancel)
	private TextView mStartSearch;
	@ViewInject(R.id.searchlist)
	private XListView mSearchlistView;
	@ViewInject(R.id.middle)
	private LinearLayout middle_layout;
	@ViewInject(R.id.grid)
	private GridView gridview;
	@ViewInject(R.id.search_layout)
	private RelativeLayout search_layout;

	List<ContactUserInfo> mSearchList = new ArrayList<ContactUserInfo>();
	ContactAdapter mSearchAdapter;
	private int search_page = 1;

	Dialog dialog;
	String code;
	String depth;
	int type;
	private BitmapUtils mBitmapUtils;
	String titleStr;
	private int page = 1;
	ContactUserData userData;
	List<ContactUserInfo> mContactUserInfos = new ArrayList<ContactUserInfo>();
	List<ContactUserInfo> mChoose = new ArrayList<ContactUserInfo>();
	List<String> Strlist = new ArrayList<String>();
	ContactThirdLevelAdapter adapter;
	private int mfrom;
	Handler handler = new Handler();
	HorizontalGridViewAdapter gridAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		init();
		getContactList();
	}

	@SuppressWarnings("unchecked")
	private void init() {
		dialog = DialogUtil.getprocessDialog(ContactThirdLevelActivity.this, "正在加载...");
		title.setText(getIntent().getStringExtra("title"));
		Strlist = (List<String>) getIntent().getSerializableExtra("Strlist");
		Strlist.add(getIntent().getStringExtra("title"));
		code = getIntent().getStringExtra("code");
		type = getIntent().getIntExtra(Const.EXECUTER_TYPE, 0);
		depth = getIntent().getStringExtra("depth").replace(".0", "");
		mfrom = getIntent().getIntExtra(Const.CONTACTS_FROM, 0);
		listview.setOnItemClickListener(this);
		listview.setPullLoadEnable(false);
		listview.setPullRefreshEnable(false);
		listview.setXListViewListener(this);
		adapter = new ContactThirdLevelAdapter(mContactUserInfos, ContactThirdLevelActivity.this, mfrom);
		listview.setAdapter(adapter);

		// mOperate.setImageResource(R.drawable.search);
		// mOperate.setVisibility(View.VISIBLE);
		mStartSearch.setText("搜索");
		mSearchAdapter = new ContactAdapter(mSearchList, this, 3, mfrom, 0);
		mSearchlistView.setAdapter(mSearchAdapter);
		mSearchlistView.setPullLoadEnable(false);
		mSearchlistView.setPullRefreshEnable(false);
		mSearchlistView.setOnItemClickListener(new MySearchListener());
		mSearchlistView.setXListViewListener(new IXListViewListener() {

			@Override
			public void onLoadMore() {
				String newSearchText = mEdtsearch.getText().toString().trim();
				if (TextUtils.isEmpty(newSearchText)) {
					ToastUtils.show(ContactThirdLevelActivity.this, "搜索内容不能为空");
					return;
				}
				getSearchUserInfo(search_page, newSearchText);
			}

			@Override
			public void onRefresh() {

			}
		});

		mChoose = (List<ContactUserInfo>) getIntent().getSerializableExtra("mChoose");
		mBitmapUtils = Util.getBitmapUtils(this, R.drawable.talk_portrait);
		if (type == 1) {
			bottom_layout.setVisibility(View.VISIBLE);
		}
		for (int i = 0; i < Strlist.size(); i++) {
			View childview = LayoutInflater.from(this).inflate(R.layout.item_folder_level, null);
			TextView text = (TextView) childview.findViewById(R.id.text);
			childview.setId(i);
			childview.setOnClickListener(new OnFolderLevelClick());
			text.setText(Strlist.get(i));
			Horizontal_layout.addView(childview);
			Horizontal_layout.invalidate();
		}
		handler.post(new Runnable() {

			@Override
			public void run() {
				mHorizontalScrollView.fullScroll(mHorizontalScrollView.FOCUS_RIGHT);
			}
		});
		setGridView();
		gridAdapter = new HorizontalGridViewAdapter(mChoose, ContactThirdLevelActivity.this);
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
		middle_layout.setVisibility(View.GONE);
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

	class MySearchListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			if (mfrom == 1) {
				// 从主菜单进入，查看联系人详情
				position--;
				Intent intent = new Intent();
				intent.setClass(ContactThirdLevelActivity.this, ContactDetailActivity.class);
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
						ToastUtils.show(ContactThirdLevelActivity.this, "不能选择自己为审批人");
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
		dialog.setMessage("您确定添加" + name + "为审批人吗？");
		dialog.setCancelable(false);
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

	public class OnFolderLevelClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (v.getId() + 1 == Strlist.size()) {
				return;
			}
			String titleStr = Strlist.get(v.getId());
			Intent intent = new Intent();
			intent.putExtra("titleStr", titleStr);
			// if (mChoose.size() > 0) {
			// intent.putExtra(Const.MCHOOSE, (Serializable) mChoose);
			// }
			setResult(30, intent);
			finish();
		}

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

	@OnClick(R.id.iv_public_back)
	private void onBackClick(View v) {
		if (mSearchLayout.isShown()) {
			search_layout.setVisibility(View.VISIBLE);
			middle_layout.setVisibility(View.VISIBLE);
			mEdtsearch.setText("");
			mSearchLayout.setVisibility(View.GONE);
			mSearchlistView.setVisibility(View.GONE);
			mSearchList.clear();
			mSearchAdapter.notifyDataSetChanged();
			search_page = 1;
			KeyBoardUtils.closeKeybord(mEdtsearch, this);
		} else {
			// if (mChoose.size() > 0) {
			Intent intent = new Intent();
			intent.putExtra(Const.MCHOOSE, (Serializable) mChoose);
			setResult(20, intent);
			// }
			finish();
		}

	}

	@SuppressWarnings("static-access")
	private void getContactList() {
		if (page == 1) {
			dialog.show();
		}
		String url = UrlUtils.getUrl("getcontactlist", page, 30, code, Integer.parseInt(depth),AppLoader.getInstance().getmUserInfo().getUser_id()) + "&only=true";
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.GET, url, new ParamUtils().getBaseRequestParams(), new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				if (Parser.isSuccess(responseInfo)) {
					userData = Parser.toDataEntity(responseInfo, ContactUserData.class);
					if (userData == null) {
						return;
					} else {
						mContactUserInfos.addAll(userData.getList());
						selectmChoosedPerson();
						adapter.notifyDataSetChanged();
						if (userData.getHas_next_page() > 0) {
							listview.setPullLoadEnable(true);
						} else {
							listview.setPullLoadEnable(false);
						}
						if (page == 1 && dialog != null) {
							dialog.dismiss();
						}
						page++;
						stopLoad();
					}
				} else {
					ToastUtils.show(ContactThirdLevelActivity.this, Parser.getMsg(responseInfo.result));
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				stopLoad();
				if (page == 1 && dialog != null) {
					dialog.dismiss();
				}
				ToastUtils.show(ContactThirdLevelActivity.this, msg);
			}
		});
	}

	private void stopLoad() {
		listview.stopLoadMore();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		position--;
		if (mfrom == 1) {
			Intent intent = new Intent();
			intent.setClass(ContactThirdLevelActivity.this, ContactDetailActivity.class);
//			intent.putExtra("url", mContactUserInfos.get(position).getAvatar());
//			intent.putExtra("name", mContactUserInfos.get(position).getName());
//			intent.putExtra("nick_name", mContactUserInfos.get(position).getNick_name());
//			intent.putExtra("phone", mContactUserInfos.get(position).getMobile_phone());
//			intent.putExtra("department", mContactUserInfos.get(position).getDepartment());
//			intent.putExtra("email", mContactUserInfos.get(position).getEmail());
			intent.putExtra(Const.ACCOUNT, mContactUserInfos.get(position));
			startActivity(intent);
		}
	}

	@Override
	public void enterPersonDetail(int position) {
		// super.enterPersonDetail(position);
		Intent intent = new Intent();
		intent.setClass(ContactThirdLevelActivity.this, ContactDetailActivity.class);
		if (mSearchLayout.isShown()) {
			intent.putExtra(Const.ACCOUNT, mSearchList.get(position));
		} else {
			intent.putExtra(Const.ACCOUNT, mContactUserInfos.get(position));
		}
		startActivity(intent);
	}
	public void updateUserInfo(ContactUserInfo contactUserInfo) {
		super.updateUserInfo(contactUserInfo);
		Logger.getLogger().d("contactsThirdActivity  update");
		if (mSearchLayout.isShown()) {
			for (int i = 0; i < mSearchList.size(); i++) {
				if (contactUserInfo.getId().equals(mSearchList.get(i).getId())) {
					mSearchList.get(i).setIs_collect(contactUserInfo.getIs_collect());
					return ;
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
				ToastUtils.show(ContactThirdLevelActivity.this, "不能选择自己为审批人");
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
			// mChoose.remove(mContactUserInfos.get(position));
			// setGridView();
			// gridAdapter.notifyDataSetChanged();
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

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {
		getContactList();
	}

	/**
	 * 搜索联系人列表
	 */
	@SuppressWarnings("static-access")
	private void getSearchUserInfo(int index, String keywords) {
		dialog.show();
		HttpUtils httpUtils = new HttpUtils();
		String url = UrlUtils.getUrl("getcontactlistsearch", index, 30, code, Integer.parseInt(depth), keywords,AppLoader.getInstance().getmUserInfo().getUser_id()) + "&only=true";
		httpUtils.send(HttpMethod.GET, url, new ParamUtils().getBaseRequestParams(), new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				dialog.dismiss();
				if (Parser.isSuccess(responseInfo)) {
					ContactUserData searchdata = Parser.toDataEntity(responseInfo, ContactUserData.class);
					mSearchList.clear();
					mSearchList.addAll(searchdata.getList());
					if (mSearchList.size() == 0) {
						ToastUtils.show(ContactThirdLevelActivity.this, "没有搜索到相关联系人");
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
				ToastUtils.show(ContactThirdLevelActivity.this, msg);
			}
		});
	}

}
