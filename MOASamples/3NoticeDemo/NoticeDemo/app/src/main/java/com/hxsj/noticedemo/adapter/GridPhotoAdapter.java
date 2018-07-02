package com.hxsj.noticedemo.adapter;

import java.util.List;

import com.hxsj.noticedemo.R;
import com.hxsj.noticedemo.bean.Const;
import com.hxsj.noticedemo.util.Util;
import com.lidroid.xutils.BitmapUtils;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;


public class GridPhotoAdapter extends BaseAdapter {
	private List<String> mSelectItems;
	private Context mContext;
	// private Point mPoint = new Point(0, 0);// 用来封装ImageView的宽和高的对象
	// private GridView mGridView;
	BitmapUtils mBitmapUtils;

	public GridPhotoAdapter(Context context, List<String> selectItems) {
		this.mContext = context;
		this.mSelectItems = selectItems;
		// this.mGridView = mGridView;
		mBitmapUtils = Util.getBitmapUtils(context);
	}

	public void setData(List<String> selectItems) {
		this.mSelectItems = selectItems;
	}

	@Override
	public int getCount() {
		return mSelectItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mSelectItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_photo, null);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			holder.delIcon = (ImageView) convertView.findViewById(R.id.delIcon);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String path = mSelectItems.get(position);

		if ("add".equals(mSelectItems.get(position))) {
			holder.delIcon.setVisibility(View.INVISIBLE);
			holder.image.setImageResource(R.drawable.addimg);
		} else {
			holder.delIcon.setVisibility(View.VISIBLE);
			mBitmapUtils.display(holder.image, "file://" + path);
		}

		holder.delIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mSelectItems.remove(position);
				if (!"add".equals(mSelectItems.get(mSelectItems.size() - 1))) {
					mSelectItems.add("add");
				}
				notifyDataSetChanged();
			}
		});

		return convertView;
	}

	class ViewHolder {
		ImageView image;
		ImageView delIcon;
	}
}

