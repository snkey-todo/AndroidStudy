package com.hxsj.noticedemo.adapter;

import java.util.List;

import com.hxsj.noticedemo.ContactBaseActivity;
import com.hxsj.noticedemo.R;
import com.hxsj.noticedemo.bean.ContactUserInfo;
import com.hxsj.noticedemo.util.Util;
import com.hxsj.noticedemo.widget.CircleImage;
import com.hxsj.noticedemo.widget.CircularImage;
import com.lidroid.xutils.BitmapUtils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 联系人数据适配器
 **/
public class ContactAdapter extends BaseAdapter {

	private List<ContactUserInfo> list;
	private Context mContext;
	private BitmapUtils mBitmapUtils;
	// type类型，1表示是按字母排序的搜索，2,按部门排序的搜索，3搜索的排序
	private int type;

	// 判断是否为选人模式(from=1不是选人模式，其他都是)
	private int from;
	//判断在搜索的时候是否需要屏蔽，布局的点击事件
	private int in;

	public ContactAdapter(List<ContactUserInfo> list, Context mContext, int type, int from, int in) {
		this.list = list;
		this.mContext = mContext;
		this.type = type;
		this.from = from;
		this.in=in;
		mBitmapUtils = Util.getBitmapUtils(mContext, R.drawable.talk_portrait);
	}

	@Override
	public int getCount() {

		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_contact, null);
			holder.keySortLayout = (LinearLayout) convertView.findViewById(R.id.layout_sortKeyLayout);
			holder.layout_content = (RelativeLayout) convertView.findViewById(R.id.layout_content);
			holder.mSortKey = (TextView) convertView.findViewById(R.id.tv_sortKey);
			holder.mCheckBox = (ImageView) convertView.findViewById(R.id.chk_contactSelector);

			holder.imageView = (CircularImage) convertView.findViewById(R.id.img_header);
			holder.mNameView = (TextView) convertView.findViewById(R.id.tv_contactName);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		String string = null;
		if (type == 3) {
			holder.keySortLayout.setVisibility(View.GONE);
		} else {
			if (type == 1) {
				if (position == 0) {
					string = list.get(position).getPingyin().substring(0, 1).toUpperCase();
				} else {
					String py = list.get(position).getPingyin().substring(0, 1);
					String spy = list.get(position - 1).getPingyin().substring(0, 1);
					if (!py.equals(spy)) {
						string = list.get(position).getPingyin().substring(0, 1);
					}
				}
				if (string == null) {
					holder.keySortLayout.setVisibility(View.GONE);
				} else {
					holder.keySortLayout.setVisibility(View.VISIBLE);
					holder.mSortKey.setText(string);
				}
			}
		}

		holder.mNameView.setText(list.get(position).getName());
		mBitmapUtils.display(holder.imageView, list.get(position).getAvatar());

		holder.imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((ContactBaseActivity)mContext).enterPersonDetail(position);
			}
		});
		if (from == 1) {
			holder.mCheckBox.setVisibility(View.GONE);
		} else {
			holder.mCheckBox.setVisibility(View.VISIBLE);
			if (list.get(position).mSelect) {
				holder.mCheckBox.setImageResource(R.drawable.ico_checkbox_hover);
			} else {
				holder.mCheckBox.setImageResource(R.drawable.ico_checkbox);
			}
			if (in ==1) {
				holder.layout_content.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						((ContactBaseActivity)mContext).choosePerson(position);
					}
				});
			}
		}
		return convertView;
	}

	class Holder {
		LinearLayout keySortLayout;
		RelativeLayout layout_content;
		TextView mSortKey;
		ImageView mCheckBox;
		CircularImage imageView;
		TextView mNameView;
	}

}
