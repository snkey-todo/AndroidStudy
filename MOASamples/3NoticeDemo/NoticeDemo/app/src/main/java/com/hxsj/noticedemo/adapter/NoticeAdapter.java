package com.hxsj.noticedemo.adapter;

import java.util.List;


import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hxsj.noticedemo.R;
import com.hxsj.noticedemo.bean.Notice;
import com.hxsj.noticedemo.util.AppUtils;
import com.hxsj.noticedemo.widget.TVGlobalLayoutListener;


public class NoticeAdapter extends BaseAdapter {
	//传递Context，用来调用加载布局文件方法
	private Context mContext;
	//数据列表集合
	private List<Notice> mList;

	//构造函数
	public NoticeAdapter(Context mContext, List<Notice> mList) {
		this.mContext = mContext;
		this.mList = mList;
	}

	//返回数据列表的总数
	@Override
	public int getCount() {
		return mList.size();
	}

	//获得当前位置的Item类容
	@Override
	public Object getItem(int arg0) {
		return mList.get(arg0);
	}
	//获得位置
	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		//加载布局文件，如果为空，则加载布局，否则从缓存中获取
		if (convertView == null) {
			holder = new Holder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_notice2, null);
			holder.mtvDate = (TextView) convertView.findViewById(R.id.tv_notice_time);
			holder.mtvTitle = (TextView) convertView.findViewById(R.id.tv_notice_title);
			holder.mtvContent = (TextView) convertView.findViewById(R.id.tv_notice_content);
			holder.mtvName=(TextView)convertView.findViewById(R.id.tv_notice_name);
			holder.mImageView=(ImageView)convertView.findViewById(R.id.iv_notice);
            holder.layout=(LinearLayout)convertView.findViewById(R.id.msg_header);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.layout.setVisibility(View.VISIBLE);
		holder.mImageView.setVisibility(View.GONE);
		//填充数据适配器的内容
		holder.mtvDate.setText(AppUtils.getTimeLine(mList.get(position).getCreated_time()));
		holder.mtvTitle.setText(mList.get(position).getTitle());
		holder.mtvContent.setText(Html.fromHtml(mList.get(position).getContent()));
		holder.mtvContent.getViewTreeObserver().addOnGlobalLayoutListener(new TVGlobalLayoutListener(holder.mtvContent));
		return convertView;
	}
	//构造布局文件模型类Holder
	class Holder {
		LinearLayout layout;
		ImageView mImageView;
		TextView mtvDate;
		TextView mtvTitle;
		TextView mtvContent;
		TextView mtvName;
	}
}
