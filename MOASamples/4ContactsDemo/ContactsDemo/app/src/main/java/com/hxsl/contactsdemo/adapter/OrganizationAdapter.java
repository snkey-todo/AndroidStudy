package com.hxsl.contactsdemo.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hxsl.contactsdemo.R;
import com.hxsl.contactsdemo.bean.Contact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OrganizationAdapter extends BaseAdapter {

	List<Contact> list = new ArrayList<Contact>();
	Context context;

	public OrganizationAdapter(List<Contact> mContacts, Context context) {
		super();
		this.list = mContacts;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.item_contact_organization, null);
			holder.title=(TextView) convertView.findViewById(R.id.title);
			holder.content=(TextView) convertView.findViewById(R.id.content);
			convertView.setTag(holder);
		} else {
			holder=(ViewHolder) convertView.getTag();
		}
		holder.title.setText(list.get(position).getName());
		holder.content.setText(list.get(position).getCount()+"");
		return convertView;
	}
	
	class ViewHolder{
		ImageView image;
		TextView title;
		TextView note;
		TextView content;
	}

}
