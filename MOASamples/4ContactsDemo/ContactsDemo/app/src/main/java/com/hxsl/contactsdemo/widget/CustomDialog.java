package com.hxsl.contactsdemo.widget;

import com.hxsl.contactsdemo.R;
import com.hxsl.contactsdemo.log.Logger;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class CustomDialog extends Dialog {

	public CustomDialog(Context context) {
		super(context);
	}

	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {

		private Context context;
		private CharSequence positiveButtonText, negativeButtonText;
		private CharSequence titleText;
		private CharSequence messageText;
		private CharSequence[] items;
		private int positiveColor = 0, negativeColor = 0;

		private OnClickListener positiveButtonListener, negativeButtonListener,
				itemClickListener, defaultListener = new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						Logger.getLogger().d("i:" + i);
						dialogInterface.dismiss();
					}
				};
		private int width = 0;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setTitle(CharSequence sequence) {
			this.titleText = sequence;
			return this;
		}

		public Builder setTitle(int strId) {
			this.titleText = context.getText(strId);
			return this;
		}

		public Builder setMessage(CharSequence sequence) {
			this.messageText = sequence;
			return this;
		}

		public Builder setMessage(int strId) {
			this.messageText = context.getText(strId);
			return this;
		}

		public Builder setItems(CharSequence[] items, OnClickListener listener) {
			this.items = items;
			this.itemClickListener = listener == null ? defaultListener
					: listener;
			return this;
		}

		public Builder setItems(int itemsId, OnClickListener listener) {
			this.items = context.getResources().getTextArray(itemsId);
			this.itemClickListener = listener == null ? defaultListener
					: listener;
			return this;
		}

		public Builder setPositiveButton(CharSequence text,
				OnClickListener listener) {
			this.positiveButtonText = text;
			this.positiveButtonListener = listener == null ? defaultListener
					: listener;
			return this;
		}

		public Builder setPositiveButton(int strId, OnClickListener listener) {
			this.positiveButtonText = context.getText(strId);
			this.positiveButtonListener = listener == null ? defaultListener
					: listener;
			return this;
		}

		public Builder setPositiveButton(int strId, int colorSelector,
				OnClickListener listener) {
			this.positiveButtonText = context.getText(strId);
			this.positiveColor = colorSelector;
			this.positiveButtonListener = listener == null ? defaultListener
					: listener;
			return this;
		}

		public Builder setPositiveButton(CharSequence text, int colorSelector,
				OnClickListener listener) {
			this.positiveButtonText = text;
			this.positiveColor = colorSelector;
			this.positiveButtonListener = listener == null ? defaultListener
					: listener;
			return this;
		}

		public Builder setNegativeButton(int strId, int colorSelector,
				OnClickListener listener) {
			this.negativeButtonText = context.getText(strId);
			this.negativeColor = colorSelector;
			this.negativeButtonListener = listener == null ? defaultListener
					: listener;
			return this;
		}

		public Builder setNegativeButton(CharSequence text, int colorSelector,
				OnClickListener listener) {
			this.negativeButtonText = text;
			this.negativeColor = colorSelector;
			this.negativeButtonListener = listener == null ? defaultListener
					: listener;
			return this;
		}

		public Builder setNegativeButton(CharSequence text,
				OnClickListener listener) {
			this.negativeButtonText = text;
			this.negativeButtonListener = listener == null ? defaultListener
					: listener;
			return this;
		}

		public Builder setNegativeButton(int strId, OnClickListener listener) {
			this.negativeButtonText = context.getText(strId);
			this.negativeButtonListener = listener == null ? defaultListener
					: listener;
			return this;
		}

		public Builder setWidth(int width) {
			this.width = width;
			return this;
		}

//		public CustomDialog create() {
//			final CustomDialog dialog = new CustomDialog(context,
//					R.style.CustomDialogStyle);
//			LayoutInflater inflater = LayoutInflater.from(context);
//			View contentView = inflater.inflate(R.layout.custom_dialog, null);
//			dialog.addContentView(contentView, new ViewGroup.LayoutParams(
//					width == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : width,
//					ViewGroup.LayoutParams.WRAP_CONTENT));
//
//			TextView titleView = (TextView) contentView
//					.findViewById(R.id.tv_dialogTitle);
//			TextView messageView = (TextView) contentView
//					.findViewById(R.id.tv_dialogMsg);
//			ListView itemsListView = (ListView) contentView
//					.findViewById(R.id.listView_items);
//			Button positiveButton = (Button) contentView
//					.findViewById(R.id.btn_positive);
//			Button negativeButton = (Button) contentView
//					.findViewById(R.id.btn_negative);
//
//			// 赋值和添加事件
//			if (TextUtils.isEmpty(titleText)) {
//				((View) titleView.getParent()).setVisibility(View.GONE);
//			} else {
//				((View) titleView.getParent()).setVisibility(View.VISIBLE);
//				titleView.setText(titleText);
//			}
//
//			if (TextUtils.isEmpty(messageText)) {
//				messageView.setVisibility(View.GONE);
//			} else {
//				messageView.setVisibility(View.VISIBLE);
//				messageView.setText(messageText);
//			}
//
//			if (items == null || items.length == 0) {
//				itemsListView.setVisibility(View.GONE);
//			} else {
//				itemsListView.setVisibility(View.VISIBLE);
//				View parent = (View) itemsListView.getParent();
//				parent.setPadding(parent.getPaddingLeft(), 0,
//						parent.getPaddingRight(), 0);
//				String[] itemArray = new String[items.length];
//				for (int i = 0; i < items.length; i++) {
//					itemArray[i] = items[i].toString();
//				}
//				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//						context, R.layout.item_dialog_list, R.id.tv_itemText,
//						itemArray);
//				itemsListView.setAdapter(adapter);
//				itemsListView.setOnItemClickListener(new OnItemClickListener() {
//
//					@Override
//					public void onItemClick(AdapterView<?> adapter, View view,
//							int position, long id) {
//						itemClickListener.onClick(dialog, position);
//					}
//				});
//			}
//
//			if (TextUtils.isEmpty(positiveButtonText)
//					&& TextUtils.isEmpty(negativeButtonText)) {
////				((View) (positiveButton.getParent().getParent()))
////						.setVisibility(View.GONE);
//				positiveButton.setVisibility(View.GONE);
//				negativeButton.setVisibility(View.GONE);
//			} else {
//				((View) (positiveButton.getParent().getParent()))
//						.setVisibility(View.VISIBLE);
//				if (TextUtils.isEmpty(positiveButtonText)) {
//					positiveButton.setVisibility(View.GONE);
//				} else {
//					positiveButton.setVisibility(View.VISIBLE);
//					positiveButton.setText(positiveButtonText);
//					if (positiveColor != 0)
//						positiveButton.setTextColor(context.getResources()
//								.getColorStateList(positiveColor));
//					positiveButton
//							.setOnClickListener(new View.OnClickListener() {
//								@Override
//								public void onClick(View view) {
//									positiveButtonListener.onClick(dialog,
//											DialogInterface.BUTTON_POSITIVE);
//								}
//							});
//				}
//				if (TextUtils.isEmpty(negativeButtonText)) {
//					negativeButton.setVisibility(View.GONE);
//				} else {
//					negativeButton.setVisibility(View.VISIBLE);
//					negativeButton.setText(negativeButtonText);
//					if (negativeColor != 0)
//						negativeButton.setTextColor(context.getResources()
//								.getColorStateList(negativeColor));
//					negativeButton
//							.setOnClickListener(new View.OnClickListener() {
//
//								@Override
//								public void onClick(View view) {
//									negativeButtonListener.onClick(dialog,
//											DialogInterface.BUTTON_NEGATIVE);
//								}
//							});
//				}
//			}
//			dialog.setContentView(contentView);
//			return dialog;
//		}
	}

}
