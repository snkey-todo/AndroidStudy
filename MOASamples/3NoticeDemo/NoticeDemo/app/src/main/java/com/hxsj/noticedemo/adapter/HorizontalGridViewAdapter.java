package com.hxsj.noticedemo.adapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.hxsj.noticedemo.R;
import com.hxsj.noticedemo.bean.ContactUserInfo;
import com.hxsj.noticedemo.util.Util;
import com.hxsj.noticedemo.widget.CircleImage;
import com.hxsj.noticedemo.widget.CircularImage;
import com.lidroid.xutils.BitmapUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class HorizontalGridViewAdapter extends BaseAdapter {

	List<ContactUserInfo> list = new ArrayList<ContactUserInfo>();
	BitmapUtils bitmapUtils;
	Context context;

	public HorizontalGridViewAdapter(List<ContactUserInfo> list, Context context) {
		super();
		this.list = list;
		this.context = context;
		bitmapUtils=Util.getBitmapUtils(context, R.drawable.talk_portrait);
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
	public View getView(int position, View convertView, ViewGroup parent) {
		CircularImage imageview=null;
		if (convertView==null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.item_grid_circleimage, null);
			imageview=(CircularImage) convertView.findViewById(R.id.mCircleImage);
			convertView.setTag(imageview);
		} else {
			imageview=(CircularImage) convertView.getTag();
		}
		bitmapUtils.display(imageview, list.get(position).getAvatar());
		return convertView;
	}

}
