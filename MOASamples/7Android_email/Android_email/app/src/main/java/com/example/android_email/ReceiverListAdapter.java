package com.example.android_email;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ReceiverListAdapter extends BaseAdapter {

	List<Emailbean> list = new ArrayList<Emailbean>();
	Context context;

	public ReceiverListAdapter(List<Emailbean> list, Context context) {
		super();
		this.list = list;
		this.context = context;
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
	public View getView(int position, View convertView, ViewGroup group) {
		Holder holder;
		if (convertView == null) {
			holder=new Holder();
			convertView=LayoutInflater.from(context).inflate(R.layout.item, null);
			holder.title=(TextView) convertView.findViewById(R.id.title);
			holder.content=(TextView) convertView.findViewById(R.id.content);
			convertView.setTag(holder);
		}else{
			holder=(Holder) convertView.getTag();
		}
		holder.title.setText(list.get(position).getTitle());
		holder.content.setText(list.get(position).getContent());
		return convertView;
	}

	class Holder {
		TextView title;
		TextView content;
	}
}
