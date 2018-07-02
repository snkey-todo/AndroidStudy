package com.hxsl.contactsdemo.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hxsl.contactsdemo.ContactBaseActivity;
import com.hxsl.contactsdemo.R;
import com.hxsl.contactsdemo.bean.ContactUserInfo;
import com.hxsl.contactsdemo.util.Util;
import com.hxsl.contactsdemo.widget.CircularImage;
import com.lidroid.xutils.BitmapUtils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ContactThirdLevelAdapter extends BaseAdapter {

	List<ContactUserInfo> list = new ArrayList<ContactUserInfo>();
	ContactBaseActivity context;
	BitmapUtils bitmapUtils;
	private int from;

	public ContactThirdLevelAdapter(List<ContactUserInfo> list, ContactBaseActivity context, int from) {
		super();
		this.list = list;
		this.from = from;
		this.context = context;
		bitmapUtils = Util.getBitmapUtils(context, R.drawable.talk_portrait);
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
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_contact_thirdlevel, null);
			holder.image = (CircularImage) convertView.findViewById(R.id.img_header);
			holder.name = (TextView) convertView.findViewById(R.id.tv_contactName);
			holder.mCheckBox = (ImageView) convertView.findViewById(R.id.chk_contactSelector);
			holder.mContactLayout = (RelativeLayout) convertView.findViewById(R.id.layout_content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (from == 1) {
			holder.mCheckBox.setVisibility(View.GONE);
		} else {
			holder.mCheckBox.setVisibility(View.VISIBLE);
			if (list.get(position).mSelect) {
				holder.mCheckBox.setImageResource(R.drawable.ico_checkbox_hover);
			} else {
				holder.mCheckBox.setImageResource(R.drawable.ico_checkbox);
			}
			holder.mContactLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					context.choosePerson(position);
				}
			});
		}
		holder.image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				context.enterPersonDetail(position);
			}
		});
		bitmapUtils.display(holder.image, list.get(position).getAvatar());
		holder.name.setText(list.get(position).getName());
		return convertView;
	}

	class ViewHolder {
		RelativeLayout mContactLayout;
		CircularImage image;
		TextView name;
		ImageView mCheckBox;
	}
}
